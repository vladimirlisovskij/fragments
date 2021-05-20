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
import com.google.android.gms.location.LocationServices
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor (
        var retrofitBuilder: RetrofitBuilder,
        var daoBuilder: DAOBuilder,
        var contextHolder: ContextHolder,
        var activityHolder: ActivityHolder
) {
    companion object {
        private const val PREF_KEY = "repo.prefKey"
        private const val CITY_KEY = "repo.CityKey"
    }

    fun setCityId(id: Int){
        contextHolder.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit()
            .putInt(CITY_KEY, id)
            .apply()
    }

    suspend fun getServerResponse() : WeatherContainer {
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
        return container
    }

    fun getAll() : ArrayList<String> {
        val employeeList: List<Employee> = daoBuilder.getDAO().getAll()
        val res: ArrayList<String> = arrayListOf()
        employeeList.forEach {
            res.add(it.cityID)
        }
        return res
    }

    fun insert(string: String) {
        val employee = Employee(0, string, "temp")
        daoBuilder.getDAO().insert(employee)
    }
}