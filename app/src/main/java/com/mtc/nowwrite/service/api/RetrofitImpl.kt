package com.mtc.nowwrite.service.api

import com.mtc.nowwrite.utils.Constants.Companion.FCM_BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitImpl {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(FCM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy {
            retrofit.create(FCMNotificationAPI::class.java)
        }
    }
}