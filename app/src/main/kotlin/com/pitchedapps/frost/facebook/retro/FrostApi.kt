package com.pitchedapps.frost.facebook.retro

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.pitchedapps.frost.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Created by Allan Wang on 2017-05-30.
 *
 * API for data retrieval
 */
object FrostApi {

    internal lateinit var frostApi: IFrost

    operator fun invoke(context: Context) {
        val cacheDir = File(context.cacheDir, "responses")
        val cacheSize = 5L * 1024 * 1024 //10MiB
        val cache = Cache(cacheDir, cacheSize)

        val client = OkHttpClient.Builder()
                .addInterceptor(FrostInterceptor(context))
                .cache(cache)


        //add logger and stetho last

        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE == "releaseTest") {  //log if not full release
            client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            client.addNetworkInterceptor(StethoInterceptor())
        }

        val gson = GsonBuilder().setLenient()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://graph.facebook.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .client(client.build())
                .build();
        frostApi = retrofit.create(IFrost::class.java)
    }
}