package com.example.myapplication.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.myapplication.activityHolder.ActivityHolder
import com.example.myapplication.contextHolder.ContextHolder
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
        val retrofitBuilder: RetrofitBuilder,
        val daoBuilder: DAOBuilder,
        val contextHolder: ContextHolder,
        val activityHolder: ActivityHolder,
        val toster: Toster
) {
    companion object {
        private const val PREF_KEY = "repo.prefKey"
        private const val CITY_KEY = "repo.CityKey"
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
        val container: WeatherContainer = WeatherContainer()
        if (mes.isSuccessful) {
            val jDoc = mes.body();
            jDoc?.let {
                container.tempStr = "temp: " + jDoc.main?.temp.toString()
                container.humStr = "humidity: " + jDoc.main?.humidity.toString()

                container.wMainStr = "weather: " + jDoc.weather?.elementAt(0)?.main;
                container.wDescStr =
                    "weather desc: " + jDoc.weather?.elementAt(0)?.description;

                container.windStr = "wind speed: " + jDoc.wind?.speed.toString()

                val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                sdf.timeZone = TimeZone.getTimeZone("UTC")

                container.ssetStr = "sunset: " + jDoc.sys?.sunset?.let {
                    sdf.format(Date(it * 1000))
                }

                container.sriseStr = "sunrise: " + jDoc.sys?.sunrise?.let {
                    sdf.format(Date(it * 1000))
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z")
                val date = Date(System.currentTimeMillis())
                container.timeStr = formatter.format(date)

                container.name = jDoc.name
                container.id = jDoc.id
            }
        }
        return container
    }

    fun getWeather() {
            val prefs = contextHolder
                .getContext()
                .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

            if (!prefs.contains(CITY_KEY)) {
                if (ActivityCompat.checkSelfPermission(
                        contextHolder.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        contextHolder.getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    toster.makeToast("geolocation not enabled")
                    weatherCallback?.invoke(WeatherContainer())
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
                if (daoBuilder.getDAO().contains(intId).isEmpty())
                {
                    withContext(Dispatchers.Main) {
                        insertCallback?.invoke(employee)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        insertCallback?.invoke(null)
                    }
                }
                daoBuilder.getDAO().insert(employee)
            }
        }
    }
}