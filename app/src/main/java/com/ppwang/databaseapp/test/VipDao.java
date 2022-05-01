package com.ppwang.databaseapp.test;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.ppwang.databaseapp.entity.VipEntity;

import java.util.List;

/***
 * @author: vitar5
 * @time: 2022/5/1
 */
@Dao
public abstract class VipDao {

    /**
     * 插入对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(VipEntity... obj);

    /**
     * 查询所有列表数据
     */
    public List<VipEntity> findAll() {
        return findAll(new SimpleSQLiteQuery("SELECT * FROM VipEntity"));
    }

    /**
     * 通过Sql自定义语句执行查找
     */
    @RawQuery
    public abstract List<VipEntity> findAll(SupportSQLiteQuery sqlLiteQuery);

}
