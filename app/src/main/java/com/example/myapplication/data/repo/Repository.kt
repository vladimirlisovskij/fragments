package com.example.myapplication.data.repo

import android.content.Context
import android.location.Location
import android.os.Handler
import com.example.myapplication.data.ktor.JSONData
import com.example.myapplication.data.ktor.KtorBuilder
import com.example.myapplication.data.ktor.WeatherContainer
import com.example.myapplication.data.sqlite.SQLiteModule
import com.example.myapplication.domain.interactors.api.APIRepo
import com.example.myapplication.domain.interactors.database.DBRepo
import com.example.myapplication.presenter.injectApplication.MainApplication
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class Repository @Inject constructor (
    private val ktorBuilder: KtorBuilder,
    private val sqLiteModule: SQLiteModule
)   : APIRepo
    , DBRepo
{
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

    private var weatherCB: ((WeatherContainer) -> Unit)? = null
    private var apiCB: ((ArrayList<SQLiteModule.City>) -> Unit)? = null
    private var insertCB: ((SQLiteModule.City?) -> Unit)? = null
    private var locationCB: (() -> Unit)? = null
    private var toastCB: ((String) -> Unit)? = null

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    private val apiHandler = CoroutineExceptionHandler { _, throwable ->
        toastCB?.invoke(throwable.localizedMessage ?: "BUG")
    }

    private val weatherExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toastCB?.invoke(throwable.localizedMessage ?: "BUG")
        Handler(MainApplication.getInstance().mainLooper).post {
            weatherCB?.invoke(WeatherContainer())
        }
    }

    private val insertExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        toastCB?.invoke(throwable.localizedMessage ?: "BUG")
        Handler(MainApplication.getInstance().mainLooper).post {
            insertCB?.invoke(null)
        }
    }

    override fun setCityId(id: Int){
        MainApplication.getInstance().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE).edit()
            .putInt(CITY_KEY, id)
            .apply()
    }

    override fun setToastCallback(callBack: (String) -> Unit) {
        toastCB = callBack
    }

    override fun setGetCallback(callBack: (ArrayList<SQLiteModule.City>) -> Unit) {
        apiCB = callBack
    }

    override fun setInsertCallback(callBack: (SQLiteModule.City?) -> Unit) {
        insertCB = callBack
    }

    override fun setLocationCallback(callback: () -> Unit) {
        locationCB = callback
    }

    override fun getLocationCallback(): (Location?) -> Unit {
        return this::setLocation
    }

    override fun setWeatherCallback(callBack: (WeatherContainer) -> Unit) {
        weatherCB = callBack
    }

    private fun parseMes(mes: JSONData) : WeatherContainer {
        val container = WeatherContainer()
        mes.let {
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
        return container
    }

    private fun setLocation(location: Location?) {
        val lat = location?.latitude ?: 0.0
        val lon = location?.longitude ?: 0.0
        scope.launch(Dispatchers.IO + weatherExceptionHandler) {
            val container = parseMes(ktorBuilder.getGeoRequest(lat, lon))
            container.id?.let {
                setCityId(Integer.valueOf(it))
                SQLiteModule.City(Integer.valueOf(it), container.name ?: "unknown")
                insert(it)
            }
            withContext(Dispatchers.Main) {
                weatherCB?.invoke(container)
            }
        }
    }

    override fun getWeather() {
        val prefs = MainApplication
            .getInstance()
            .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)

        if (!prefs.contains(CITY_KEY)) {
            locationCB?.invoke()
        } else {
            scope.launch(Dispatchers.IO + weatherExceptionHandler) {
                val curCityID = MainApplication
                    .getInstance()
                    .getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
                    .getInt(CITY_KEY, 6295630)

                withContext(Dispatchers.Main) {
                    weatherCB?.invoke(parseMes(ktorBuilder.getIDRequest(curCityID)))
                }
            }
        }
    }

    override fun getApi() {
        scope.launch(Dispatchers.IO + apiHandler) {
            val cityList: ArrayList<SQLiteModule.City> = sqLiteModule.getAll()
            withContext(Dispatchers.Main) {
                apiCB?.invoke(cityList)
            }
        }
    }

    override fun insert(id: String) {
        scope.launch(Dispatchers.IO + insertExceptionHandler) {
            val intId = Integer.valueOf(id)
            val container = parseMes(ktorBuilder.getIDRequest(intId))
            if (container.name == null) {
                withContext(Dispatchers.Main) {
                    insertCB?.invoke(null)
                }
            } else {
                val city = SQLiteModule.City(intId, container.name!!)
                val notEmpty = sqLiteModule.contains(intId)
                withContext(Dispatchers.Main) {
                    insertCB?.invoke(
                        when(notEmpty) {
                            true -> null
                            false-> city
                        }
                    )
                }
                sqLiteModule.insert(intId, container.name!!)
            }
        }
    }
}