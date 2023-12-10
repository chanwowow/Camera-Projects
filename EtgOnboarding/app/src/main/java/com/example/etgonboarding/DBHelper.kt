package com.example.etgonboarding

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context) : SQLiteOpenHelper(context, "chanwow.db", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        val sql = "create table chanwowTable (" +
                "idx integer primary key autoincrement, " +
                "textData text not null, " +
                "intData integer not null, "+
                "floatData real not null, "+
                "dateData date not null"+
                ")"

        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}