package com.example.myapplication.data.sqlite

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.presenter.injectApplication.MainApplication
import javax.inject.Inject

class SQLiteModule @Inject constructor()
    : SQLiteOpenHelper(MainApplication.getInstance(), NAME, null, VERSION) {

    class City (
        val ID: Int,
        val Name: String
    )

    private companion object {
        const val NAME = "myDB"
        const val VERSION = 1

        const val TABLE_NAME = "cities"
        const val CITY_COL = "cityName"

        const val CREATE_COMMAND = "create table $TABLE_NAME (id integer primary key," +
                "$CITY_COL integer);"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    fun getAll() : ArrayList<City> {
        val res = arrayListOf<City>()
        val cursor = writableDatabase.query(TABLE_NAME, null, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                res.add(City(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex(CITY_COL)))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    fun contains(id: Int) : Boolean{
        val cursor = writableDatabase.query(TABLE_NAME, null, "id = $id", null, null, null, null, null)
        val res = cursor.count > 0
        cursor.close()
        return res
    }

    fun insert(id: Int, city: String) {
        writableDatabase.insert(TABLE_NAME, null,
            ContentValues().apply {
                put("id", id)
                put(CITY_COL, city)
        })
    }
}