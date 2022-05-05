package com.ppwang.bundle.database.core

import com.ppwang.bundle.database.service.PPDefaultService

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
class PPDatabase private constructor() {

    companion object {
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

    fun <T> getService(clazz: Class<T>): PPDefaultService<T>? {
        return null
    }

}