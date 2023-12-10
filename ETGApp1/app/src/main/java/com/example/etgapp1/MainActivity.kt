package com.example.etgapp1

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Content Provider (App1 code)
        val buttonRead = findViewById<Button>(R.id.buttonRead)
        val textViewDB = findViewById<TextView>(R.id.textViewDB)
        buttonRead.setOnClickListener {
            val uri = Uri.parse("content://com.example.etgonboarding.mycontentprovider")

            // if you put "null" in condition param, it means getting all the data without condition
            val c = contentResolver.query(uri, null, null, null, null)

            textViewDB.text = ""
            while(c!!.moveToNext()){
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

        }

        val buttonInsert = findViewById<Button>(R.id.buttonInsert)
        buttonInsert.setOnClickListener{
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.format(Date())

            val cv1 = ContentValues()
            cv1.put("textData", "Str1 (app2)")
            cv1.put("intData", 777)
            cv1.put("floatData", 1.18)
            cv1.put("dateData", date)
            val cv2 = ContentValues()
            cv2.put("textData", "Str2 (app2)")
            cv2.put("intData", 888)
            cv2.put("floatData", 3.14)
            cv2.put("dateData", date)

            val uri = Uri.parse("content://com.example.etgonboarding.mycontentprovider")

            contentResolver.insert(uri, cv1)
            contentResolver.insert(uri, cv2)
        }

    }
}