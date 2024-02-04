package com.example.filesavingtemp

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectedImg = findViewById<ImageView>(R.id.imageView)
        val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri->
            if(uri != null){
                selectedImg.setImageURI(uri)
            }
            else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        // Image Import
        val buttonImport = findViewById<Button>(R.id.button_import)
        buttonImport.setOnClickListener{
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Image Export
        val buttonExport = findViewById<Button>(R.id.button_export)
        buttonExport.setOnClickListener {
            // jpeg -> Image View -> Bitmap -> jpeg
            val bitmap = (selectedImg.drawable as BitmapDrawable).bitmap

            exportBimapFile(bitmap)
        }
    }


    fun exportBimapFile(bitmapInput : Bitmap) {
        val fileName = "FileName" + System.currentTimeMillis().toString() + ".jpg"
        val saveLocation = Environment.DIRECTORY_PICTURES + File.separator + "My Folder"
        // or you can just write like  {saveLocation = "Pictures/Folder Name"}
        // If you wanna set dir to DOWNLOADS change like this
        // DIRECTORY_PICTURES -> DIRECTORY_DOWNLOADS & MediaStore.Images.Media -> MediaStore.Downloads

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, saveLocation)
        }

        try{
            // If no dir? it creates
            // Inserts a row into a table at the given URL.
            val uri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            // Open an outputStream with the uri
            val fos = this.contentResolver.openOutputStream(uri!!)

            //Write a compressed version of the bitmap to the specified outputstream.
            bitmapInput.compress(Bitmap.CompressFormat.JPEG, 90, fos!!)

            // flush() : Buffer에 있는 모든 data를 출력stream으로 내보냄(=파일에 쓰여짐)
            // 그러나 이미 위의 compress() 에서 stream에 파일은 기록되었다. 이 코드 flush()는 없어도 되긴함
            fos.flush()
            fos.close()
            Toast.makeText(this, "File saved to $saveLocation", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            val a = e.toString()
        }

    }
}