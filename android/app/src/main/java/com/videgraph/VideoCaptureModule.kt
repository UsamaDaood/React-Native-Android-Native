// VideoCaptureModule.kt
package com.videocaptureapp

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.HashMap

class VideoCaptureModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {
    private var videoFilePath: String? = null
    private var capturePromise: Promise? = null

    init {
        reactContext.addActivityEventListener(this)
    }

    override fun getName(): String {
        return "VideoCaptureModule"
    }

    @ReactMethod
    fun captureVideo(promise: Promise) {
        capturePromise = promise
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20)
        if (takeVideoIntent.resolveActivity(reactApplicationContext.packageManager) != null) {
            val videoFile: File
            try {
                videoFile = createVideoFile()
                val videoURI = FileProvider.getUriForFile(reactApplicationContext, "${reactApplicationContext.packageName}.fileprovider", videoFile)
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                currentActivity?.startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            } catch (ex: IOException) {
                promise.reject("ERROR", "Failed to create video file")
            }
        }
    }

    @Throws(IOException::class)
    private fun createVideoFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = reactApplicationContext.getExternalFilesDir(null)!!
        return File.createTempFile("VIDEO_${timeStamp}_", ".mp4", storageDir).apply {
            videoFilePath = absolutePath
        }
    }

    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(videoFilePath)
            if (file.exists()) {
                capturePromise?.resolve(videoFilePath)
            } else {
                capturePromise?.reject("ERROR", "Video file not found")
            }
        } else {
            capturePromise?.reject("ERROR", "Video capture failed")
        }
    }

    override fun onNewIntent(intent: Intent?) {}

    companion object {
        const val REQUEST_VIDEO_CAPTURE = 1
    }
}
