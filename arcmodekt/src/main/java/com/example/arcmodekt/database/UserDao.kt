package com.example.arcmodekt.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arcmodekt.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE id = :id")
     fun findUser(id :String): LiveData<User>
}