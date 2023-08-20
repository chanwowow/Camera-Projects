package com.example.cameraxbasics

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    var capture: ImageButton? = null
    var toggleFlash: ImageButton? = null
    var flipCamera: ImageButton? = null
    private var previewView: PreviewView? = null
    var cameraFacing = CameraSelector.LENS_FACING_BACK

    private val activityResultLauncher =
        registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                startCamera(cameraFacing)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.cameraPreview)
        capture = findViewById(R.id.capture)
        toggleFlash = findViewById(R.id.toggleFlash)
        flipCamera = findViewById(R.id.flipCamera)

        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 123)

        // Camera Permission Request
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera(cameraFacing)
        }

        // Camera (Front/Back) Switching
        flipCamera!!.setOnClickListener(View.OnClickListener {
            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            startCamera(cameraFacing)
        })
    }

    fun startCamera(cameraFacing: Int) {
        val aspectRatio = aspectRatio(previewView!!.width, previewView!!.height)
        val listenableFuture = ProcessCameraProvider.getInstance(this)
        listenableFuture.addListener({
            try {
                val cameraProvider = listenableFuture.get() as ProcessCameraProvider
                val preview = Preview.Builder().setTargetAspectRatio(aspectRatio).build()
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetRotation(windowManager.defaultDisplay.rotation).build()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing).build()
                cameraProvider.unbindAll()
                val camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                capture!!.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 123)
                    } else {
                        takePicture(imageCapture)
                    }
                }
                toggleFlash!!.setOnClickListener { setFlashIcon(camera) }
                preview.setSurfaceProvider(previewView!!.surfaceProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    fun takePicture(imageCapture: ImageCapture) {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val saveLocation = Environment.DIRECTORY_DCIM + File.separator + "CCAM"

        // Create the directory if it doesn't exist
        val directory = File(saveLocation)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.RELATIVE_PATH, saveLocation)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val outputFileOptions1 = ImageCapture.OutputFileOptions.Builder(
            getContentResolver(),
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()

        imageCapture.takePicture(
            outputFileOptions1,
            Executors.newCachedThreadPool(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Image saved at: ${saveLocation}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    startCamera(cameraFacing)
                }

                override fun onError(exception: ImageCaptureException) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to save: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    startCamera(cameraFacing)
                }
            })

    }

    private fun setFlashIcon(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                toggleFlash!!.setImageResource(R.drawable.round_flash_off_24)
            } else {
                camera.cameraControl.enableTorch(false)
                toggleFlash!!.setImageResource(R.drawable.round_flash_on_24)
            }
        } else {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Flash is not available currently",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = Math.max(width, height).toDouble() / Math.min(width, height)
        return if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }
}