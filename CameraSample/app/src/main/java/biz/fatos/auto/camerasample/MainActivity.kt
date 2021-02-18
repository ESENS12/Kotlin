package biz.fatos.auto.camerasample

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture


class MainActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    val REQUEST_IMAGE_CAPTURE = 1
    val PERMISSION_REQUEST_CODE = 200
    lateinit var cameraPreviewView: PreviewView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreviewView = findViewById(R.id.cameraPreviewView)as PreviewView;
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener(Runnable {
//            val cameraProvider = cameraProviderFuture.get()
//            bindPreview(cameraProvider)
//        }, ContextCompat.getMainExecutor(this))


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (checkPermission()) {
            dispatchTakePictureIntent();
        } else {
            requestPermission();
        }
    }


    fun bindPreview(cameraProvider: ProcessCameraProvider) {

        var preview: Preview = Preview.Builder()
                .build()

        var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

        preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }


    private fun dispatchTakePictureIntent() {

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
//        }
    }

    private fun checkPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            false
        } else true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                finishAffinity();
            }
        }
    }

    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this);
        builder.setTitle("앱 종료")
        builder.setMessage("종료 하시겠습니까?")
        builder.setPositiveButton("예") { _, _ -> finishAffinity() }
        builder.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.show();

//        super.onBackPressed()
    }
}