package com.example.etgonboarding

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log

class MyContentProvider : ContentProvider() {

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // If you don't implement anything in this method, the user from app2 just cannot delete
        // Nothing happens. That's it
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val helper = DBHelper(context!!)
        val db = helper.writableDatabase

        db.insert("chanwowTable", null, values)

        return uri
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        Log.d("walaoeh", "context in query11: ${context.toString()}")
        val helper = DBHelper(context!!)
        val db = helper.writableDatabase

        val rtr = db.query("chanwowTable", projection, selection, selectionArgs, null, null, sortOrder)

        Log.d("walaoeh", "RTR : $rtr")
        return rtr
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}