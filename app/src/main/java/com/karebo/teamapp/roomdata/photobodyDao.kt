package com.karebo.teamapp.roomdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface photobodyDao {
    @Query("Select * from photobody")
    fun getAllphotobody():List<photobody>

    @Query("DELETE FROM photobody ")
    fun deletephotobody()

    @Insert
    fun addphotobody(photobody: photobody)

    @Update
    fun updatephotobody(photobody: photobody)
}