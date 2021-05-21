package com.example.myapplication.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.activityHolder.ActivityHolder
import com.example.myapplication.contextHolder.ContextHolder
import com.example.myapplication.mainActivity.MainActivity
import com.example.myapplication.retrofit.JSONData
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.retrofit.ServerApi
import com.example.myapplication.retrofit.WeatherContainer
import com.example.myapplication.room.DAOBuilder
import com.example.myapplication.room.Employee
import com.example.myapplication.toster.Toster
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Repository @Inject constructor (
        private val retrofitBuilder: RetrofitBuilder,
        private val daoBuilder: DAOBuilder,
        private val contextHolder: ContextHolder,
        private val activityHolder: ActivityHolder,
        private val toster: Toster
) {
    companion object {
        private const val PREF_KEY = "repo.prefKey"
        private const val CITY_KEY = "repo.CityKey"

        private const val TEMP_LAB = "temp: "
        private const val HUM_LAB = "humidity: "
        private const val WMAIN_LAB = "weather: "
        private const val WDESC_LAB = "weather desc: "
        private const val WIND_LAB = "wind speed: "
        private const val SSET_LAB = "sunset: "
        private const val SRISE_LAB = "sunrise: "
        private const val TIME_LAB = ""
        private const val NAME_LAB = ""
    }

    private fun getPerm(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(contextHolder.getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activityHolder.getActivity(), arrayOf(permission), requestCode)
        }
    }

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toster.makeToast(throwable.localizedMessage ?: "BUG")
    }


    fun watch() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(daoBuilder, "dao")
        appWatcher.expectWeaklyReachable(retrofitBuilder, "retro")
        appWatcher.expectWeaklyReachable(contextHolder, "cpntext")
        appWatcher.expectWeaklyReachable(activityHolder, "activity")
        appWatcher.expectWeaklyReachable(toster, "toster")
    }


    var weatherCallback: ((WeatherContainer) -> Unit)? = null
    var apiCallback: ((ArrayList<Employee>) -> Unit)? = null
    var insertCallback: ((Employee?) -> Unit)? = null

    fun setCityId(id: Int){
        contextHolder.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit()
            .putInt(CITY_KEY, id)
            .apply()
    }

    private fun parseMes(mes: Response<JSONData>) : WeatherContainer {
        val container = WeatherContainer()
        if (mes.isSuccessful) {
            val jDoc = mes.body();
            jDoc?.let {
                val sunTime = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                sunTime.timeZone = TimeZone.getTimeZone("UTC")
                val cutTime = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z", Locale.ENGLISH)
                cutTime.timeZone = TimeZone.getTimeZone("UTC")

                container.tempStr = TEMP_LAB + it.main?.temp.toString()
                container.humStr = HUM_LAB + it.main?.humidity.toString()
                container.wMainStr = WMAIN_LAB + it.weather?.elementAt(0)?.main;
                container.wDescStr = WDESC_LAB + it.weather?.elementAt(0)?.description;
                container.windStr = WIND_LAB + it.wind?.speed.toString()
                container.ssetStr = SSET_LAB + it.sys?.sunset?.let {sunTime.format(Date(it * 1000))}
                container.sriseStr = SRISE_LAB + it.sys?.sunrise?.let {sunTime.format(Date(it * 1000))}
                container.timeStr = TIME_LAB + cutTime.format(Date(System.currentTimeMillis()))
                container.name = NAME_LAB + it.name
                container.id = it.id
            }
        }
        return container
    }

    fun getWeather() {
            val prefs = contextHolder
                .getContext()
                .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

            if (!prefs.contains(CITY_KEY)) {
                getPerm(Manifest.permission.ACCESS_FINE_LOCATION, MainActivity.FINE_LOC_CODE)
                getPerm(Manifest.permission.ACCESS_COARSE_LOCATION, MainActivity.COARSE_LOC_CODE)
                if (ActivityCompat.checkSelfPermission(
                        contextHolder.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        contextHolder.getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    scope.launch(Dispatchers.IO + exceptionHandler) {
                        val mes = retrofitBuilder
                            .getRetrofit()
                            .create(ServerApi::class.java)
                            .getMessage(ServerApi.getGeoRequest(0.0, 0.0))

                        withContext(Dispatchers.Main) {
                            weatherCallback?.invoke(parseMes(mes))
                        }
                    }
                    return
                }
                LocationServices.getFusedLocationProviderClient(activityHolder.getActivity()).lastLocation.addOnSuccessListener {
                    scope.launch(Dispatchers.IO + exceptionHandler) {
                        val mes: Response<JSONData> = if (it == null) {
                            retrofitBuilder
                                .getRetrofit()
                                .create(ServerApi::class.java)
                                .getMessage(ServerApi.getRequest(693805))
                        } else {
                            val lat = it.latitude
                            val lon = it.longitude
                            retrofitBuilder
                                .getRetrofit()
                                .create(ServerApi::class.java)
                                .getMessage(ServerApi.getGeoRequest(lat, lon))
                        }

                        val container = parseMes(mes)
                        container.id?.let {
                            setCityId(Integer.valueOf(it))
                            Employee(Integer.valueOf(it).toLong(), container.name ?: "unknown")
                            insert(it)
                        }
                        withContext(Dispatchers.Main) {
                            weatherCallback?.invoke(container)
                        }
                    }
                }
            } else {
                scope.launch(Dispatchers.IO + exceptionHandler) {
                    val curCityID = contextHolder
                        .getContext()
                        .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
                        .getInt(CITY_KEY, 693805)

                    val mes = retrofitBuilder
                        .getRetrofit()
                        .create(ServerApi::class.java)
                        .getMessage(ServerApi.getRequest(curCityID))

                    withContext(Dispatchers.Main) {
                        weatherCallback?.invoke(parseMes(mes))
                    }
            }
        }
    }

    fun getApi() {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            val employeeList: ArrayList<Employee> = ArrayList(daoBuilder.getDAO().getAll())
            withContext(Dispatchers.Main) {
                apiCallback?.invoke(employeeList)
            }
        }
    }

    fun insert(id: String) {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            val intId = Integer.valueOf(id)
            val mes = retrofitBuilder
                .getRetrofit()
                .create(ServerApi::class.java)
                .getMessage(ServerApi.getRequest(intId))
            val container = parseMes(mes)
            if (container.name == null) {
                withContext(Dispatchers.Main) {
                    insertCallback?.invoke(null)
                }
            } else {
                val employee = Employee(intId.toLong(), container.name!!)
                val isEmpty = daoBuilder.getDAO().contains(intId).isEmpty()
                withContext(Dispatchers.Main) {
                    insertCallback?.invoke(
                        when(isEmpty) {
                            false -> null
                            true -> employee
                        }
                    )
                }
                daoBuilder.getDAO().insert(employee)
            }
        }
    }
}