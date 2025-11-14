package com.example.lab_week_10.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Total::class], version = 1)
abstract class TotalDatabase : RoomDatabase() {
    abstract fun totalDao(): TotalDao
}