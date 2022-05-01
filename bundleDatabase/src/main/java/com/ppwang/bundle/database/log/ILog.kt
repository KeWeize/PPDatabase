package com.ppwang.bundle.database.log

import android.util.Log

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
internal object ILog {

    const val TAG = "PPDatabase"

    fun d(msg: String?) = d(TAG, msg)

    fun d(tag: String, msg: String?) {
        msg?.let {
            // 打印日志
            Log.d(tag, msg)
        }
    }

}