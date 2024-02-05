package com.manoj.clean.picker

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.manoj.clean.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MediaUtils {
    @RequiresApi(Build.VERSION_CODES.P)
    fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap? {
        try {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri);
            return ImageDecoder.decodeBitmap(source);
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return null
    }

    /**
     * get file name from uri
     * @param context context
     * @param uri uri
     * @return file name
     * @throws IOException
     */
    fun getMakeFile(context: Context, suffix: String): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            suffix,  /* suffix */
            storageDir /* directory */
        )
    }

    fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + context.getString(R.string.provider_authority_suffix),
            file
        )
    }

    fun scaleBitmap(inputBitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var newWidth = inputBitmap.width
        var newHeight = inputBitmap.height
        // Calculate the aspect ratios to maintain the aspect ratio of the original bitmap
        if (newWidth > maxWidth || newHeight > maxHeight) {
            val aspectRatio = newWidth.toFloat() / newHeight.toFloat()
            if (aspectRatio > 1) {
                newWidth = maxWidth
                newHeight = (newWidth / aspectRatio).toInt()
            } else {
                newHeight = maxHeight
                newWidth = (newHeight * aspectRatio).toInt()
            }
        }
        // Scale the bitmap
        return Bitmap.createScaledBitmap(inputBitmap, newWidth, newHeight, true)
    }

    /**
     * share bitmap to other apps
     * @param context context to start activity
     * @param bitmap bitmap to share
     */
    fun shareBitmapToOtherApps(context: Context, bitmap: Bitmap): String? {
        try {
            val name = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(Date())
            val cachePath = File(context.externalCacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/$name.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            val imagePath = File(context.externalCacheDir, "images")
            val newFile = File(imagePath, "$name.png")
            val contentUri = FileProvider.getUriForFile(
                context,
                context.packageName + context.getString(R.string.provider_authority_suffix),
                newFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            context.startActivity(Intent.createChooser(shareIntent, "Share"))
            return null
        } catch (e: Exception) {
            return e.message
        }
    }

    /**
     * save bitmap to file
     * @param context context to start activity
     * @param bitmap bitmap to save
     */
    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        quality: Int = 100,
        onError: ((Exception) -> Unit)? = null
    ): File? {
        try {
            val name = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US).format(Date())
            val cachePath = File(context.externalCacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/$name.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
            stream.close()
            val imagePath = File(context.externalCacheDir, "images")
            return File(imagePath, "$name.png")
        } catch (e: Exception) {
            e.printStackTrace()
            onError?.invoke(e)
        }
        return null
    }
}
