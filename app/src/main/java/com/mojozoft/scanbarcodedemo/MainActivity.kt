package com.mojozoft.scanbarcodedemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.util.SparseArray

import android.view.SurfaceHolder
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

import kotlinx.android.synthetic.main.activity_main.*


/**
 * ref : https://codelabs.developers.google.com/codelabs/bar-codes/#0
 */
class MainActivity : AppCompatActivity() {
    var barcodeDetector: BarcodeDetector? = null
    val requestCameraPermissionId = 1001
    var scanCounter = 0
    var cameraSource: CameraSource? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build()

        v_cameraPreview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource?.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    return
                cameraSource?.start(v_cameraPreview.holder)
            }
        })
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val qrcode: SparseArray<Barcode> = p0?.detectedItems as SparseArray<Barcode>

                if (qrcode.size() != 0) {
                    v_text_result.post {
                        run {
                            val vibrator: Vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                            }
                            v_text_result.text = qrcode.valueAt(0).displayValue
                            scanCounter++
                            v_text_counter.text = scanCounter.toString()

                        }
                    }
                }
            }

        })

    }

}
