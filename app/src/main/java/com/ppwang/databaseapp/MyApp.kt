package com.ppwang.databaseapp

import android.app.Application

/***
 * @author: vitar5
 * @time: 2022/5/3
 */
class MyApp : Application() {

    companion object {

        @JvmStatic
        lateinit var mApplicationContext: Application

    }

    override fun onCreate() {
        super.onCreate()
        mApplicationContext = this
    }
}