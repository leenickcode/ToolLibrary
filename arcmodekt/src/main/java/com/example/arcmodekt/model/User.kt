package com.example.arcmodekt.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

/**
 * @ClassName User
 * @Description TODO
 * @Author nick
 * @Date 2021/11/25 17:20
 * @Version 1.0
 */
@Entity
data class User(
    val admin: Boolean=false,

    val coinCount: Int=0,
    val email: String="",
    val icon: String="",
    @NotNull
    @PrimaryKey val id: Int,
    val nickname: String="",
    val password: String="",
    val publicName: String="",
    val token: String="",
    val type: Int=0,
    val username: String=""
)