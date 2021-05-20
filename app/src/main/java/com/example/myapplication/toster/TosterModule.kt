package com.example.myapplication.toster

import android.widget.Toast
import com.example.myapplication.activityHolder.ActivityHolder
import com.example.myapplication.contextHolder.ContextHolder
import javax.inject.Inject

class Toster @Inject constructor(
    var contextHolder: ContextHolder,
    var activityHolder: ActivityHolder

) {
    fun makeToast(string: String) {
        activityHolder.getActivity().runOnUiThread(
            Runnable {
                Toast.makeText(contextHolder.getContext(), string, Toast.LENGTH_SHORT).show()
            }
        )
    }
}