package com.ppwang.bundle.database.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ppwang.bundle.database.constant.DatabaseConfig
import com.ppwang.bundle.database.log.ILog
import java.lang.reflect.ParameterizedType

/***
 * 模块内部默认实现的通用 Dao 实现类，
 * 提供常规的数据库操作api，如增、删、改、查
 * @author: vitar5
 * @time: 2022/4/30
 */
abstract class PPInternalDao<T> {

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
     * 插入单个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T): Long

    /**
     * 插入多个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(objList: List<T>): List<Long>

    /**
     * 删除单个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    @Delete
    abstract fun delete(obj: T): Int

    /**
     * 删除多个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    @Delete
    abstract fun delete(objList: List<T>): Int

    /**
     * 更新单个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    @Update
    abstract fun update(obj: T): Int

    /**
     * 更新多个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    @Update
    abstract fun update(objList: List<T>): Int

    /**
     * 查询表中的全部数据
     */
    fun selectAll(): List<T>? {
        return selectAll(SimpleSQLiteQuery("SELECT * FROM $mTableName"))
    }

    /**
     * 通过查询条件查询表中的数据，SQL格式如下：“SELECT * WHERE sex = 女 FROM %tableName%”
     * 查询语句必须存在查询表名占位符号
     */
    fun selectBy(simpleSQLQuery: String): List<T>? {
        if (!simpleSQLQuery.contains(DatabaseConfig.CONST_TABLE_NAME_PLACEHOLDER)) {
            // 查询语句格式错误
            throw IllegalArgumentException("查询语句格式错误：缺少表名占位符'${DatabaseConfig.CONST_TABLE_NAME_PLACEHOLDER}'")
        }
        val sqlQuery =
            simpleSQLQuery.replace(DatabaseConfig.CONST_TABLE_NAME_PLACEHOLDER, mTableName)
        return selectAll(SimpleSQLiteQuery(sqlQuery))
    }

    @RawQuery
    abstract fun selectAll(querySql: SupportSQLiteQuery): List<T>?

}