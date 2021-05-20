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

    fun getWeather() {
        scope.launch(Dispatchers.IO + exceptionHandler) {
            // TODO add async geolocation with callback

            val curCityID = contextHolder
                .getContext()
                .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
                .getInt(CITY_KEY, 693805)

            val mes = retrofitBuilder.getRetrofit().create(ServerApi::class.java).getMessage(ServerApi.getRequest(curCityID))
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

                    container.name = "name: " + jDoc.name
                }
            }
            withContext(Dispatchers.Main) {
                weatherCallback?.invoke(container)
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