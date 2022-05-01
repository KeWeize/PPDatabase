package com.ppwang.bundle.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ppwang.bundle.database.log.ILog
import java.lang.reflect.ParameterizedType

/***
 * 模块内部默认实现的通用 Dao 实现类，
 * 提供常规的数据库操作api，如增、删、改、查
 * @author: vitar5
 * @time: 2022/4/30
 */
internal abstract class PPInternalDao<T> {

    /**
     * 根据 Entity 泛型类获取对应表名
     */
    private val mTableName: String
        get() {
            val clazz = (javaClass.superclass!!.genericSuperclass as ParameterizedType)
                .actualTypeArguments[0] as Class<*>
            val tableName = clazz.simpleName
            ILog.d("PPInternalDao init == tabName is $tableName")
            return tableName
        }

    /**
     * 插入对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg obj: T): Long

    /**
     * 查询所有列表数据
     */
    @RawQuery
    fun findAll() = findAll(SimpleSQLiteQuery("SELECT * FROM $mTableName"))


    /**
     * 通过Sql自定义语句执行查找
     */
    @RawQuery
    abstract fun findAll(sqlLiteQuery: SupportSQLiteQuery): List<T>?

}