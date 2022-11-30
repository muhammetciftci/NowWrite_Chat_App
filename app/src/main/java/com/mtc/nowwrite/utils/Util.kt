package com.mtc.nowwrite.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mtc.nowwrite.R
import com.mtc.nowwrite.model.PushNotification
import com.mtc.nowwrite.service.api.RetrofitImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class Util {

    companion object {

        fun toastMessage(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun connectionControl(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
            return isConnected
        }

        fun getHourNow(): String {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val currentDate = simpleDateFormat.format(Date()).toString()
            return currentDate
        }


        fun isValidEmail(text: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }


        fun sendNotification(notification: PushNotification) =
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitImpl.api.postNotification(notification)
                    if (response.isSuccessful) {
                        Log.d(ContentValues.TAG, "Response: ${Gson().toJson(response)}")
                    } else {
                        Log.e(ContentValues.TAG, response.errorBody().toString())
                    }
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, e.toString())
                }
            }


        fun bottomNavActive(activity: Activity) {
            val views = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            views.show()
        }

        fun bottomNavInActive(activity: Activity) {
            val views = activity.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            views.gone()
        }


        fun isAppForeGround(context: Context): Boolean {
            val mActivityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val l = mActivityManager.runningAppProcesses
            for (info in l) {
                if (info.uid == context.applicationInfo.uid && info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true
                }
            }
            return false
        }


    }

}