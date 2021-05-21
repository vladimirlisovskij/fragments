package com.example.myapplication.repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
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

    var weatherCallback: ((WeatherContainer) -> Unit)? = null
    var apiCallback: ((ArrayList<String>) -> Unit)? = null

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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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
            val employeeList: List<Employee> = daoBuilder.getDAO().getAll()
            val res: ArrayList<String> = arrayListOf()
            employeeList.forEach {
                res.add(it.cityID)
            }
            withContext(Dispatchers.Main) {
                apiCallback?.invoke(res)
            }
        }
    }

    fun insert(string: String) {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            val employee = Employee(0, string, "temp")
            daoBuilder.getDAO().insert(employee)
        }
    }
}