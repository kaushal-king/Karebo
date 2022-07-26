package com.karebo.teamapp.roomdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [mainbody::class,photobody::class], version = 1)
abstract class RoomDb : RoomDatabase(){
    abstract fun mainbodydao(): mainbodyDao?
    abstract fun photobodydao(): photobodyDao?


    companion object {
        private var INSTANCE: RoomDb? = null

        fun getAppDatabase(context: Context): RoomDb? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<RoomDb>(
                    context.applicationContext,
                    RoomDb::class.java,
                    "BodyDB"
                ).allowMainThreadQueries().build()

            }
            return INSTANCE
        }

    }

}