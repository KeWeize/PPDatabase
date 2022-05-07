package com.ppwang.bundle.database.core

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ppwang.bundle.database.service.PPDefaultService
import com.ppwang.bundledatabase_runtime.PPTableInfo
import com.ppwang.bundledatabase_runtime.constant.PPClazzConstant

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
class PPDatabase private constructor() {

    companion object {

        @JvmStatic
        internal var mAppContext: Context? = null

        @Volatile
        private var me: PPDatabase? = null

        @JvmStatic
        fun getInstance(): PPDatabase {
            if (me == null) {
                synchronized(PPDatabase::class.java) {
                    if (me == null) {
                        me = PPDatabase()
                    }
                }
            }
            return me!!
        }
    }

    fun init(appContext: Context) {
        mAppContext = appContext.applicationContext
    }

    fun <T> getService(clazz: Class<T>): PPDefaultService<T>? {
        val tableInfo = PPTableInfo(null, clazz.simpleName)
        val serviceClazzQualifiedName = tableInfo.serviceClazzQualifiedName
        Log.d("PPDatabase", "simpleName: ${clazz.simpleName}, serviceImpl: $serviceClazzQualifiedName")
        val serviceClazz = Class.forName(serviceClazzQualifiedName)
        return serviceClazz.newInstance() as PPDefaultService<T>?
    }

    fun getContext() = mAppContext

}