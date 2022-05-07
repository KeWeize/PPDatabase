package com.ppwang.bundle.database.service

import com.ppwang.bundle.database.constant.DatabaseConfig
import com.ppwang.bundle.database.dao.PPInternalDao
import java.lang.IllegalArgumentException

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
abstract class PPDefaultService<T> {

    private val tag = PPDefaultService::class.java.simpleName

    private var mDaoImpl: PPInternalDao<T>? = null
        get() {
            if (field == null) {
                field = attachIntenalDaoImpl()
            }
            return field
        }

    abstract fun attachIntenalDaoImpl(): PPInternalDao<T>?

    /**
     * 插入单个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(obj: T): Long {
        return mDaoImpl?.insert(obj) ?: -1
    }

    /**
     * 插入多个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(vararg obj: T): List<Long> {
        val list = ArrayList<T>(obj.size)
        obj.forEach { item ->
            list.add(item)
        }
        return mDaoImpl?.insert(list) ?: listOf()
    }

    /**
     * 插入多个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(objList: List<T>): List<Long> {
        return mDaoImpl?.insert(objList) ?: listOf()
    }

    /**
     * 删除单个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(obj: T): Int {
        return mDaoImpl?.delete(obj) ?: 0
    }

    /**
     * 删除多个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(vararg obj: T): Int {
        val list = ArrayList<T>(obj.size)
        obj.forEach { item ->
            list.add(item)
        }
        return mDaoImpl?.delete(list) ?: 0
    }

    /**
     * 删除多个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(objList: List<T>): Int {
        return mDaoImpl?.delete(objList) ?: 0
    }

    /**
     * 更新单个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(obj: T): Int {
        return mDaoImpl?.update(obj) ?: 0
    }

    /**
     * 更新多个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(vararg obj: T): Int {
        val list = ArrayList<T>(obj.size)
        obj.forEach { item ->
            list.add(item)
        }
        return mDaoImpl?.update(list) ?: 0
    }

    /**
     * 更新多个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(objList: List<T>): Int {
        return mDaoImpl?.update(objList) ?: 0
    }

    /**
     * 查询表中的全部数据
     */
    fun selectAll(): List<T>? {
        return mDaoImpl?.selectAll()
    }

    /**
     * 通过查询条件查询表中的数据，SQL格式如下：“SELECT * WHERE sex = 女 FROM %tableName%”
     * 查询语句必须存在查询表名占位符号
     */
    fun selectBy(simpleSQLQuery: String): List<T>? {
        if (!simpleSQLQuery.contains(DatabaseConfig.CONST_TABLE_NAME_PLACEHOLDER)) {
            // 查询语句格式错误
            throw IllegalArgumentException("查询语句格式错误：缺少表名占位符${DatabaseConfig.CONST_TABLE_NAME_PLACEHOLDER}")
        }
        return mDaoImpl?.selectBy(simpleSQLQuery)
    }

}