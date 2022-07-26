package com.karebo.teamapp.roomdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "mainbody")
data class mainbody(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String = "",
    @SerializedName("body")
    @ColumnInfo(name = "body")
    val body:String,
    ): Serializable
