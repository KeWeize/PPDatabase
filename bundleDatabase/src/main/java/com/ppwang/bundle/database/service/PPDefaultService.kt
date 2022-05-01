package com.ppwang.bundle.database.service

import com.ppwang.bundle.database.constant.DatabaseConfig
import com.ppwang.bundle.database.log.ILog
import java.lang.IllegalArgumentException

/***
 * @author: vitar5
 * @time: 2022/4/30
 */
abstract class PPDefaultService<T> {

    private val tag = PPDefaultService::class.java.simpleName

    /**
     * 插入单个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(obj: T): Long {
        ILog.d(tag, "insert(1)")
        return 0L
    }

    /**
     * 插入多个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(vararg obj: T): List<Long> {
        ILog.d(tag, "insert(2)")
        return listOf()
    }

    /**
     * 插入多个对象到数据库，如果插入目标对象主键id已存在则会覆盖旧项
     * @return 插入项新的 rowId
     */
    fun insert(objList: List<T>): List<Long> {
        ILog.d(tag, "insert(3)")
        return listOf()
    }

    /**
     * 删除单个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(obj: T): Int {
        ILog.d(tag, "delete(1)")
        return 0
    }

    /**
     * 删除多个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(vararg obj: T): Int {
        ILog.d(tag, "delete(2)")
        return 0
    }

    /**
     * 删除多个对象，使用主键进行匹配删除
     * @return 成功执行了删除的行数
     */
    fun delete(objList: List<T>): Int {
        ILog.d(tag, "delete(3)")
        return 0
    }

    /**
     * 更新单个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(obj: T): Int {
        ILog.d(tag, "update(1)")
        return 0
    }

    /**
     * 更新多个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(vararg obj: T): Int {
        ILog.d(tag, "update(2)")
        return 0
    }

    /**
     * 更新多个对象，使用主键进行匹配更新
     * @return 成功执行了更新的行数
     */
    fun update(objList: List<T>): Int {
        ILog.d(tag, "update(3)")
        return 0
    }

    /**
     * 查询表中的全部数据
     */
    fun selectAll(): List<T>? {
        ILog.d(tag, "selectAll")
        return listOf()
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
        return listOf()
    }

}