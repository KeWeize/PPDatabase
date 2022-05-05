package com.ppwang.databaseapp.test;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ppwang.bundle.database.dao.impl.PPVipEntityDaoImpl;
import com.ppwang.databaseapp.entity.VipEntity;

/***
 * @author: vitar5
 * @time: 2022/5/1
 */
//@Database(entities = {VipEntity.class}, exportSchema = false, version = 1)
public abstract class PPRoomDatabase extends RoomDatabase {

    public abstract PPVipEntityDaoImpl getVipEntityDaoImpl();

}
