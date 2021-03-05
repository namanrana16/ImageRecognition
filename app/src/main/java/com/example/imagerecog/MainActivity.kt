package com.example.imagerecog


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import com.google.mlkit.vision.common.InputImage as InputImage1
import kotlin.collections.mutableListOf as mutableListOf1

typealias LumaListener = (luma: Double) -> Unit


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    val recognizer = TextRecognition.getClient()
    var listIng= ArrayList<String>()
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray
        ) {
            if (requestCode == REQUEST_CODE_PERMISSIONS) {
                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }





        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

     private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override public fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                    Log.d(TAG, msg)
                    var ingredients = mutableListOf1<String>()
                    var ingredientsText: String = ""

                    val image: InputImage1 = InputImage1.fromFilePath(applicationContext, savedUri)
                    val result = recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Task completed successfully
                            // ...
                            for (block in visionText.textBlocks) {

                                val boundingBox = block.boundingBox
                                val cornerPoints = block.cornerPoints
                                val text = block.text
                                // Toast.makeText(baseContext, text, Toast.LENGTH_SHORT).show()
                                Log.i(toString(), text)
                                for (line in block.lines) {
                                    // ...
                                    // val elementText = line.text
                                    //  Toast.makeText(baseContext, elementText, Toast.LENGTH_SHORT).show()
                                    for (element in line.elements) {
                                        val elementText = element.text
                                        //  Toast.makeText(baseContext, elementText, Toast.LENGTH_SHORT).show()
                                        if (elementText == "INGREDIENTS:" || elementText == "Ingredients:") {
                                            Toast.makeText(
                                                baseContext,
                                                "Found ingredients",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            ingredientsText = text
                                        }

                                        // ...
                                    }
                                }
                            }

                            var text: String
                            val finalText = ArrayList<String>()
                            val t: Int = ingredientsText.length
                            var i: Int = 0
                            while (i != t) {
                                text = ""
                                for (k in i..t - 1) {
                                    if (ingredientsText[k] == ',') {
                                        break
                                    }
                                    if (ingredientsText[k] == ' ') {
                                        continue
                                    }

                                    if (ingredientsText[k] == '.') {
                                        break
                                    }

                                    if (ingredientsText[k] == ':') {
                                        break
                                    }

                                    text += ingredientsText[k]
                                    i = k
                                }
                                i++
                                finalText.add(text)

                            }
                            val finalText2 = ArrayList<String>()
                            finalText.remove(" ")
                            var k = 0
                            for (i in 0..finalText.size - 1) {
                                if (finalText[i] == "" || finalText[i] == "Ingredients") {
                                    continue
                                }
                                finalText2.add(finalText[i])
                                k++
                            }
                                listIng = finalText2
                            for (i in finalText2) {
                                Toast.makeText(baseContext, i, Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            // ...
                        }


                    list()
                }
            })
    }
  
    
   public fun list()
    {
        val intent = Intent(this, MainActivity2::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList("mylist",listIng)
        intent.putExtras(bundle)
        startActivity(intent)


    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }



    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }




}

private fun Bundle.putParcelableArrayList(s: String, listIng: ArrayList<String>) {

}




