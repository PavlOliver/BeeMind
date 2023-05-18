package com.example.beemind.databaseData

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Class is used to create tables which are used in a project
 *
 * @constructor creates a database with given name
 *
 * @param context
 */
class Database(context: Context) : SQLiteOpenHelper(context, "hivesStorage", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS HIVES(title TEXT PRIMARY KEY,address VARCHAR(17))")
        db?.execSQL("CREATE TABLE CURE(title TEXT,date DATE,temperature REAL,job TEXT NOT NULL,weight REAL,hiveTemp REAL,PRIMARY KEY(title,date))")
        db?.execSQL("CREATE TABLE INSPECTION(title TEXT,date DATE,temperature REAL,job TEXT,weight REAL,hiveTemp REAL,queen VARCHAR(2) CHECK(queen in ('M+','M-')), PRIMARY KEY(title,date))")
        db?.execSQL("CREATE TABLE MONITORING(title TEXT,date DATE,temperature REAL,weight REAL, hiveTemp REAL,humidity REAL,PRIMARY KEY(title,date))")
        db?.execSQL("CREATE TABLE TASK(task TEXT PRIMARY KEY)")
        db?.execSQL("CREATE TABLE OWNER(owner TEXT,reg_num CHAR(6) PRIMARY KEY,email TEXT,phone TEXT,svfa TEXT,lat REAL,lon REAL)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}