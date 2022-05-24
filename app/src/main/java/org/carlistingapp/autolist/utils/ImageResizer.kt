package org.carlistingapp.autolist.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ImageResizer {
    //For Image Size 640*480, use MAX_SIZE =  307200 as 640*480 307200
    //private static long MAX_SIZE = 360000;
    //private static long THUMB_SIZE = 6553;
    fun reduceBitmapSize(bitmap : Bitmap, MAX_SIZE: Int): Bitmap {
        val ratioSquare: Double
        val bitmapHeight: Int = bitmap.height
        val bitmapWidth: Int = bitmap.width
        ratioSquare = bitmapHeight * bitmapWidth / MAX_SIZE.toDouble()
        if (ratioSquare <= 1) return bitmap
        val ratio = sqrt(ratioSquare)
        Log.d("mylog", "Ratio: $ratio")
        val requiredHeight = (bitmapHeight / ratio).roundToInt().toInt()
        val requiredWidth = (bitmapWidth / ratio).roundToInt().toInt()
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)

    }

    fun generateThumb(bitmap: Bitmap, THUMB_SIZE: Int): Bitmap {
        val ratioSquare: Double
        val bitmapHeight: Int = bitmap.height
        val bitmapWidth: Int = bitmap.width
        ratioSquare = bitmapHeight * bitmapWidth / THUMB_SIZE.toDouble()
        if (ratioSquare <= 1) return bitmap
        val ratio = sqrt(ratioSquare)
        Log.d("mylog", "Ratio: $ratio")
        val requiredHeight = (bitmapHeight / ratio).roundToInt().toInt()
        val requiredWidth = (bitmapWidth / ratio).roundToInt().toInt()
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true)
    }

    fun exifToDegrees(exifOrientation: Int): Int {
        when (exifOrientation) {
            (ExifInterface.ORIENTATION_ROTATE_90) -> {
                return 90
            }

            (ExifInterface.ORIENTATION_ROTATE_180) -> {
                return 180
            }

            (ExifInterface.ORIENTATION_ROTATE_270) -> {
                return 270
            }
        }
        return 0
    }
}