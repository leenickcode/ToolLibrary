package com.example.arcmodekt.database

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.blankj.utilcode.util.LogUtils
import com.example.arcmodekt.model.User
import java.io.File


/**
 * <pre>
 *     author : zhangx
 *     time   : 2021/07/13
 *     desc   : 数据库管理类
 *     version: 1.0
 * </pre>
 */
@Database(entities = [User::class], version = 1)
abstract class RoomDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(@NonNull database: SupportSQLiteDatabase) {
                LogUtils.d("数据库升级")
                //执行升级相关操作
                database.execSQL("ALTER TABLE GatherInfo" + " ADD COLUMN xjzt TEXT")
            }
        }


        private var instance: RoomDataBase? = null
        fun getInstance(context: Context): RoomDataBase {
            if (instance == null) {
                synchronized(RoomDataBase::class.java) {
                    if (instance == null) {

                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            RoomDataBase::class.java,
                            "database-name"//数据库名称
                        )
                            .allowMainThreadQueries()
//                            .addMigrations(MIGRATION_2_3)
//
                            .build()

                    }
                }
            }
            return instance as RoomDataBase
        }
    }
}