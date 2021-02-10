package com.example.fetchgituserdetailsretroandvolley.retroFit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitCallback {

    /*@GET("/users/{user}")
    fun getgituserdata(@Path): Call<gitBean>
*/
    @GET("/users/{user}")
    fun GetUser(@Path("user") user: String): Call<gitBean>

    companion object Factory{
        fun create(): RetrofitCallback {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build()
            return retrofit.create(RetrofitCallback::class.java)

        }
    }
}