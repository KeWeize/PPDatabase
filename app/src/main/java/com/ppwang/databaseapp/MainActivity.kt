package com.ppwang.databaseapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.ppwang.bundle.database.core.PPDatabase
import com.ppwang.databaseapp.entity.VipEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var tag = "MainActivity"

    private lateinit var mOperateIdEt: EditText
    private lateinit var mNameEt: EditText
    private lateinit var mSexEt: EditText
    private lateinit var mAgeEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PPDatabase.getInstance().init(this)

        mOperateIdEt = findViewById(R.id.et_operate_id)
        mNameEt = findViewById(R.id.et_name)
        mSexEt = findViewById(R.id.et_sex)
        mAgeEt = findViewById(R.id.et_age)

        findViewById<View>(R.id.btn_add).setOnClickListener {
            // 新增
            Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show()
            onInsert()
        }

        findViewById<View>(R.id.btn_delete).setOnClickListener {
            // 删除
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
            onDelete()
        }

        findViewById<View>(R.id.btn_modify).setOnClickListener {
            // 修改
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
            onUpdate()
        }

        findViewById<View>(R.id.btn_check).setOnClickListener {
            // 查询
            Toast.makeText(this, "查询成功", Toast.LENGTH_SHORT).show()
            onFindAll()
        }
    }

    /**
     * 插入数据
     */
    private fun onInsert() {
        val name = mNameEt.text.toString()
        val sex = mSexEt.text.toString()
        val age = mAgeEt.text.toString().toIntOrNull() ?: 0
        val obj = VipEntity().apply {
            this.name = name
            this.sex = sex
            this.age = age
        }
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            val service = PPDatabase.getInstance().getService(VipEntity::class.java)
            val insertId = service?.insert(obj)
            Log.d(tag, "insert id is : $insertId")
        }
    }

    /**
     * 删除数据
     */
    private fun onDelete() {
        val id = mOperateIdEt.text.toString()
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            val service = PPDatabase.getInstance().getService(VipEntity::class.java)
            service?.delete(VipEntity().apply { this.uId = id.toIntOrNull() ?: 0 })
        }
    }

    /**
     * 修改数据
     */
    private fun onUpdate() {
        val id = mOperateIdEt.text.toString()
        val name = mNameEt.text.toString()
        val sex = mSexEt.text.toString()
        val age = mAgeEt.text.toString().toIntOrNull() ?: 0
        val obj = VipEntity().apply {
            this.uId = id.toIntOrNull() ?: 0
            this.name = name
            this.sex = sex
            this.age = age
        }
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            val service = PPDatabase.getInstance().getService(VipEntity::class.java)
            service?.update(obj)
        }
    }

    /**
     * 查找所有数据
     */
    private fun onFindAll() {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {

            scope.launch {
                val service = PPDatabase.getInstance().getService(VipEntity::class.java)
                val list = service?.selectBy("SELECT * FROM %tableName%")
                list?.forEach {
                    Log.d(
                        "MainActivity",
                        "id: ${it.uId}, name: ${it.name}, sex: ${it.sex}, age: ${it.age}"
                    )
                }
            }
        }
    }

}