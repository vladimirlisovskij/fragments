package com.example.myapplication.repo

//import com.example.myapplication.room.DaggerDAOComponent
import android.content.Context
import com.example.myapplication.contextHolder.ContextHolder
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.retrofit.ServerApi
import com.example.myapplication.retrofit.WeatherContainer
import com.example.myapplication.room.DAOBuilder
import com.example.myapplication.room.Employee
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor (
        var retrofitBuilder: RetrofitBuilder,
        var daoBuilder: DAOBuilder,
        var contextHolder: ContextHolder
) {
    companion object {
        private val PREF_KEY = "repo.prefKey"
        private val CITY_KEY = "repo.CITYKey"
    }

    fun setCityId(id: Int){
        contextHolder.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).let {
            it.edit()
                .putInt(CITY_KEY, id)
                .apply()
        }
    }

    suspend fun getServerResponse() : WeatherContainer {
        var curCityID = 693805
        contextHolder.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).let {
            curCityID = it.getInt(CITY_KEY, curCityID)
        }

        val mes = retrofitBuilder.getRetrofit().create(ServerApi::class.java).getMessage(ServerApi.getRequest(curCityID));
        var container: WeatherContainer = WeatherContainer()
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