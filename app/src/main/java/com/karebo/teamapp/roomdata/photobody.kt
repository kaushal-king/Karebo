package com.karebo.teamapp.roomdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import com.google.gson.annotations.SerializedName


@Entity(tableName = "photobody")
data class photobody  (
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String = "",
    @SerializedName("body")
    @ColumnInfo(name = "body")
    val body:String,
): Serializable