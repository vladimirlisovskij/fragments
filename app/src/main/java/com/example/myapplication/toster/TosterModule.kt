package com.example.myapplication.toster

import android.os.Handler
import android.widget.Toast
import com.example.myapplication.injectApplication.MainApplication
import javax.inject.Inject

class Toster @Inject constructor() {
    private val handler =  Handler(MainApplication.getInstance().mainLooper)
    fun makeToast(string: String) {
        handler.post {
            Toast.makeText(MainApplication.getInstance(), string, Toast.LENGTH_SHORT).show()
        }
    }
}