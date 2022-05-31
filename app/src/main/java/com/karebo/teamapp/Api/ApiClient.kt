package com.karebo.teamapp.Api


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class ApiClient {
    val BASE_URL: String = "https://karebomobileapi.azurewebsites.net/api/"

 private var retrofit: Retrofit? = null

    var interceptorHttp = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    var okHttpClient: OkHttpClient = OkHttpClient()
         .newBuilder()
         .connectTimeout(1, TimeUnit.MINUTES)
         .readTimeout(30, TimeUnit.SECONDS)
         .writeTimeout(15, TimeUnit.SECONDS)
         .addInterceptor(interceptorHttp)
    .build()

    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit
    }


    fun getClient2(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofit
    }

}