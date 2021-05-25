package com.example.myapplication.repo

import android.content.Context
import android.location.Location
import android.os.Handler
import com.example.myapplication.injectApplication.MainApplication
import com.example.myapplication.retrofit.JSONData
import com.example.myapplication.retrofit.RetrofitBuilder
import com.example.myapplication.retrofit.ServerApi
import com.example.myapplication.retrofit.WeatherContainer
import com.example.myapplication.room.DAOBuilder
import com.example.myapplication.room.Employee
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
        private val daoBuilder: DAOBuilder
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

    var weatherCallback: ((WeatherContainer) -> Unit)? = null
    var apiCallback: ((ArrayList<Employee>) -> Unit)? = null
    var insertCallback: ((Employee?) -> Unit)? = null
    var locationCallback: (() -> Unit)? = null
    var toastCallback: ((String) -> Unit)? = null

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    private val apiHandler = CoroutineExceptionHandler { _, throwable ->
        toastCallback?.invoke(throwable.localizedMessage ?: "BUG")
    }

    private val weatherExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toastCallback?.invoke(throwable.localizedMessage ?: "BUG")
        Handler(MainApplication.getInstance().mainLooper).post {
            weatherCallback?.invoke(WeatherContainer())
        }
    }

    private val insertExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toastCallback?.invoke(throwable.localizedMessage ?: "BUG")
        Handler(MainApplication.getInstance().mainLooper).post {
            insertCallback?.invoke(null)
        }
    }

    fun watch() {
        val appWatcher: ObjectWatcher = AppWatcher.objectWatcher
        appWatcher.expectWeaklyReachable(daoBuilder, "dao")
        appWatcher.expectWeaklyReachable(retrofitBuilder, "retro")
    }

    fun setCityId(id: Int){
        MainApplication.getInstance().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit()
            .putInt(CITY_KEY, id)
            .apply()
    }

    private fun parseMes(mes: Response<JSONData>) : WeatherContainer {
        val container = WeatherContainer()
        if (mes.isSuccessful) {
            val jDoc = mes.body()
            jDoc?.let {
                val sunTime = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                sunTime.timeZone = TimeZone.getTimeZone("UTC")
                val cutTime = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z", Locale.ENGLISH)
                cutTime.timeZone = TimeZone.getTimeZone("UTC")

                container.tempStr = TEMP_LAB + it.main?.temp.toString()
                container.humStr = HUM_LAB + it.main?.humidity.toString()
                container.wMainStr = WMAIN_LAB + it.weather?.elementAt(0)?.main
                container.wDescStr = WDESC_LAB + it.weather?.elementAt(0)?.description
                container.windStr = WIND_LAB + it.wind?.speed.toString()
                container.ssetStr = SSET_LAB + it.sys?.sunset?.let { sunset -> sunTime.format(Date(sunset * 1000))}
                container.sriseStr = SRISE_LAB + it.sys?.sunrise?.let { sunrise -> sunTime.format(Date(sunrise * 1000))}
                container.timeStr = TIME_LAB + cutTime.format(Date(System.currentTimeMillis()))
                container.name = NAME_LAB + it.name
                container.id = it.id
            }
        }
        return container
    }

    fun setLocation(location: Location?) {
        val lat = location?.latitude ?: 0.0
        val lon = location?.longitude ?: 0.0
        scope.launch(Dispatchers.IO + weatherExceptionHandler) {
            val mes: Response<JSONData> = retrofitBuilder
                .getRetrofit()
                .create(ServerApi::class.java)
                .getMessage(ServerApi.getGeoRequest(lat, lon))

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

    fun getWeather() {
        val prefs = MainApplication
            .getInstance()
            .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

        if (!prefs.contains(CITY_KEY)) {
            locationCallback?.invoke()
        } else {
            scope.launch(Dispatchers.IO + weatherExceptionHandler) {
                val curCityID = MainApplication
                    .getInstance()
                    .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
                    .getInt(CITY_KEY, 6295630)

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
        scope.launch(Dispatchers.IO + apiHandler) {
            val employeeList: ArrayList<Employee> = ArrayList(daoBuilder.getDAO().getAll())
            withContext(Dispatchers.Main) {
                apiCallback?.invoke(employeeList)
            }
        }
    }

    fun insert(id: String) {
        scope.launch(Dispatchers.IO + insertExceptionHandler) {
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