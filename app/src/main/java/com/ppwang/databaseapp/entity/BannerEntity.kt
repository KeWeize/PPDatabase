package com.ppwang.databaseapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
@Entity
class BannerEntity {

    @PrimaryKey
    var uId: Int? = null

    @ColumnInfo(name = "adType")
    var adType: String? = null

    @ColumnInfo(name = "storeId")
    var storeId: String? = null

    @ColumnInfo(name = "starTime")
    var starTime: Long? = 0

    @ColumnInfo(name = "endTime")
    var endTime: Long? = 0

}