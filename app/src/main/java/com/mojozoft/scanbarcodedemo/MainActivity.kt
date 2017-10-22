package com.mojozoft.scanbarcodedemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
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
    var scanCounter = 0
    var cameraSource: CameraSource? = null
    var surfaceCreated = false
    var code = ""
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        setContentView(R.layout.activity_main)
        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource = CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).setAutoFocusEnabled(true).build()
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {

            }

            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val qrcode: SparseArray<Barcode> = p0?.detectedItems as SparseArray<Barcode>

                if (qrcode.size() != 0) {
                    v_text_result.post {
                        run {
                            var newCode = qrcode.valueAt(0).displayValue
                            if (code != newCode) {
                                code = newCode
                                scanCounter++
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    vibrator?.vibrate(100)

                                }
                                v_text_result.text = newCode
                                v_text_counter.text = scanCounter.toString()
//                            beep()
                            }


                        }
                    }
                }
            }

        })
        v_cameraPreview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                surfaceCreated = false
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                surfaceCreated = true
                if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    return
                cameraSource?.start(v_cameraPreview.holder)
            }
        })

    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return
        if (surfaceCreated) {
            cameraSource?.start(v_cameraPreview.holder)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.stop();
    }

    fun beep() {
        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 50);
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_0, 500);
    }

}
