package com.example.etgonboarding

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var editTextUrl : EditText? = null
    var textViewHttp : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. SQLite (2 ways)
        val textViewDB = findViewById<TextView>(R.id.textViewDB)

        val buttonInsertDB = findViewById<Button>(R.id.buttonInsert1)
        buttonInsertDB.setOnClickListener {
            val helper = DBHelper(this)
            val db = helper.writableDatabase

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.format(Date())

            // -- How #1 : Using SQL
            val sql = "insert into chanwowTable (textData, intData, floatData, dateData) values (?, ?, ?, ?)"

            val arg1 = arrayOf("Str1", "119", "1.18", date)
            val arg2 = arrayOf("str2", "183", "3.14", date)

            db.execSQL(sql, arg1)
            db.execSQL(sql, arg2)
            // --

            /* -- HOW #2 : Using provided class
            val cv1 = ContentValues()
            cv1.put("textData", "Str1")
            cv1.put("intData", 119)
            cv1.put("floatData", 1.18)
            cv1.put("dateData", date)
            val cv2 = ContentValues()
            cv2.put("textData", "Str2")
            cv2.put("intData", 183)
            cv2.put("floatData", 3.14)
            cv2.put("dateData", date)

            db.insert("chanwowTable", null, cv1)
            db.insert("chanwowTable", null, cv2)
             */ // --

            db.close()
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()
        }

        val buttonReadDB = findViewById<Button>(R.id.buttonRead1)
        buttonReadDB.setOnClickListener{
            val helper = DBHelper(this)
            val db = helper.writableDatabase

            // -- How #1 : Using SQL
            val sql = "select * from chanwowTable"
            val c: Cursor = db.rawQuery(sql, null)
            // --

            /* -- HOW #2 : Using provided class
            val c = db.query("chanwowTable", null, null, null, null, null, null)
             */

            textViewDB.text = " "
            while(c.moveToNext()){
                val idx_pos = c.getColumnIndex("idx")
                val textData_pos = c.getColumnIndex("textData")
                val intData_pos = c.getColumnIndex("intData")
                val floatData_pos = c.getColumnIndex("floatData")
                val dateData_pos = c.getColumnIndex("dateData")

                val idx = c.getInt(idx_pos)
                val textData = c.getString(textData_pos)
                val intData = c.getInt(intData_pos)
                val floatData = c.getFloat(floatData_pos)
                val dateData = c.getString(dateData_pos)

                textViewDB.append("idx : $idx \n")
                textViewDB.append("textData : $textData \n")
                textViewDB.append("intData : $intData \n")
                textViewDB.append("floatData : $floatData \n")
                textViewDB.append("dateData : $dateData \n \n")
            }

            db.close()
        }

        val buttonUpdateDB = findViewById<Button>(R.id.buttonUpdate1)
        buttonUpdateDB.setOnClickListener{
            val helper = DBHelper(this)
            val db = helper.writableDatabase

            // -- How #1 : Using SQL
            val sql = "update chanwowTable set textData=? where idx=?"
            val args = arrayOf("Updated Str3", "1")
            db.execSQL(sql, args)
            // --

            /* -- HOW #2 : Using provided class
            val cv = ContentValues()
            cv.put("textData", "Updated Str3")
            val where = "idx=?"
            val args = arrayOf("1")
            db.update("chanwowTable", cv, where, args)
            */

            db.close()
            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
        }

        val buttonDeleteDB = findViewById<Button>(R.id.buttonDelete1)
        buttonDeleteDB.setOnClickListener{
            val helper = DBHelper(this)
            val db = helper.writableDatabase

            // -- How #1 : Using SQL
            val sql = "delete from chanwowTable where idx=?"
            val args = arrayOf("1") // delete item of idx 1
            db.execSQL(sql, args)
            // --

            /* -- HOW #2 : Using provided class
            val where = "idx=?"
            val args = arrayOf("1")
            db.delete("chanwowTable", where, args)
             */

            db.close()
            Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
        }


        // 2. Content Provider  => Refer to app 1's additional subActivity code

        // 3. Network (Socket)  -> Skip

        // 3. Network (Http)
        editTextUrl = findViewById(R.id.editTextUrl)
        textViewHttp = findViewById(R.id.textViewHttp)
        val buttonGetData = findViewById<Button>(R.id.buttonGetData)
        buttonGetData.setOnClickListener{
            val thread = MyNetworkThread()
            thread.start()
        }
    }

    inner class MyNetworkThread : Thread(){
        override fun run() {
            var data = " "
            try{
                // Connect first
                val site = editTextUrl?.text.toString()
                val url = URL(site)
                val conn = url.openConnection()

                // Make stream to read string line by line
                val input = conn.getInputStream()
                val isr = InputStreamReader(input, "UTF-8")
                val br = BufferedReader(isr)

                var str:String? = null
                val buf = StringBuffer()

                do{ // Read line by line
                    str = br.readLine()
                    if(str!=null){
                        buf.append(str)
                    }
                } while(str != null)

                data = buf.toString()
            } catch (e: MalformedURLException) {
                data = "Invalid URL" // Handle invalid URL
            } catch (e: IOException) {
                data = "Network error happened" // Handle IO exception
            }

            runOnUiThread{
                textViewHttp?.text = data
            }
        }
    }

}