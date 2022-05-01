package com.ppwang.databaseapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
@Entity
class VipEntity {

    @PrimaryKey
    var uId: Int? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "sex")
    var sex: String? = null

    @ColumnInfo(name = "age")
    var age: Int? = 0

}