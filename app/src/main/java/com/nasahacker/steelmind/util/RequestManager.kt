package com.nasahacker.steelmind.util

import com.nasahacker.steelmind.listener.ResponseListener
import com.nasahacker.steelmind.model.Quote
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

object RequestManager {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.QUOTES.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val service by lazy {
        retrofit.create(ApiService::class.java)
    }


    private interface ApiService {
        @GET(Constants.QUOTES.ENDPOINT)
        fun fetchQuotes(): Call<List<Quote>>
    }

    fun getQuotes(listener: ResponseListener<List<Quote>>) {
        service.fetchQuotes().enqueue(object : Callback<List<Quote>> {
            override fun onResponse(
                data: Call<List<Quote>?>,
                response: Response<List<Quote>?>
            ) {

                if (!response.isSuccessful) {
                    listener.onFailure(response.message())
                    return
                }

                listener.onSuccess(response.body()!!, response.message())

            }

            override fun onFailure(
                data: Call<List<Quote>?>,
                t: Throwable
            ) {
                listener.onFailure(t.localizedMessage.toString())
            }

        })
    }

}