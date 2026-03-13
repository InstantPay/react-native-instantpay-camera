package com.instantpaycamera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.UIManagerHelper

object CommanHelper {

    const val MAIN_LOG_TAG = "*IpayCamera -> "

    const val WARNING_LOG = "WARNING_LOG"

    const val ERROR_LOG = "ERROR_LOG"

    /**
     * Simple Log print Method
     */
    fun logPrint(classTag:String, value: String?) {
        if (value == null) {
            return
        }

        val fullTagName = "$MAIN_LOG_TAG $classTag"

        if(BuildConfig.IpayCamera_Log){
            Log.d(fullTagName, value)
        }
    }

    /**
     * Log print method with WARNING_LOG,ERROR_LOG
     */
    fun logPrint(type:String,classTag:String, value: String?) {
        if (value == null) {
            return
        }

        val fullTagName = "$MAIN_LOG_TAG $classTag"

        if(BuildConfig.IpayCamera_Log){
            if(type == "WARNING_LOG"){
                Log.w(fullTagName, value)
            } else if(type == "ERROR_LOG") {
                Log.e(fullTagName, value)
            }
            else{
                Log.d(fullTagName, value)
            }
        }
    }

    fun resizeBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {

        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val ratio = minOf(
            maxWidth.toFloat() / width,
            maxHeight.toFloat() / height
        )

        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            newHeight,
            true
        )
    }
}


