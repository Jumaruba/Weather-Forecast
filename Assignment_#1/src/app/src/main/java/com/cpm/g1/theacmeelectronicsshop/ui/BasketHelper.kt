package com.cpm.g1.theacmeelectronicsshop.ui

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DATABASE_NAME = "basket"
const val SCHEMA_VERSION = 1

class BasketHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE Product(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, brand TEXT, description TEXT, price FLOAT)")
        }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun insert(name: String, brand: String, description: String, price: Float): Long {
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("brand", brand)
        cv.put("description", description)
        cv.put("price", price)
        return writableDatabase.insert("Product", "name", cv)
    }

    fun update(id: String, name: String, brand: String, description: String, price: Float): Int {
        val cv = ContentValues()
        val args = arrayOf(id)
        cv.put("name", name)
        cv.put("brand", brand)
        cv.put("description", description)
        cv.put("price", price)
        return writableDatabase.update("Product", cv, "_id = ?", args)
    }

    fun getAll(): Cursor {
        return readableDatabase.rawQuery(
            "SELECT _id, name, brand, description, price " +
                    "FROM Product ORDER BY name",
            null
        )
    }

    fun getName(c: Cursor): String {
        return c.getString(1)
    }

    fun getBrand(c: Cursor): String {
        return c.getString(2)
    }

    fun getDescription(c: Cursor): String {
        return c.getString(3)
    }

    fun getPrice(c: Cursor): String {
        return c.getFloat(4).toString()
    }

}