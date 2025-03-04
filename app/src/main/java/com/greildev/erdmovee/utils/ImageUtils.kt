package com.greildev.erdmovee.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.greildev.erdmovee.utils.Constant.FILENAME_IMAGE_FORMAT
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object ImageUtils {
    private val timeStamp: String = SimpleDateFormat(
        FILENAME_IMAGE_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())
    private const val BYTE_SIZE = 1024
    private const val MAX_SIZE = 1000000
    private const val INITIAL_COMPRESS_QUALITY = 100
    private const val COMPRESS_QUALITY_REDUCE = 5

    fun createTempFile(context: Context): File {
        val imageSuffix = ".jpg"
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, imageSuffix, storageDir)
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)


        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(BYTE_SIZE)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality: Int = INITIAL_COMPRESS_QUALITY

        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= COMPRESS_QUALITY_REDUCE
        } while (streamLength > MAX_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    fun ImageView?.load(url: String?, @DrawableRes placeholder: Int? = null) {
        if (this == null) return
        val drawablePlaceholder = placeholder?.let { ContextCompat.getDrawable(this.context, it) }
        if (url.isNullOrBlank()) {
            setImageDrawable(drawablePlaceholder)
        } else {
            loadFromUrl(url = url, placeholder = drawablePlaceholder)
        }
    }

    fun ImageView?.load(@DrawableRes placeholder: Int) {
        if (this == null) return
        setImageDrawable(ContextCompat.getDrawable(this.context, placeholder))
    }

    fun ImageView?.loadFromUrl(
        url: String,
        placeholder: Drawable? = null,
        centerCrop: Boolean = false,
        requestOption: RequestOptions = RequestOptions(),
        diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.AUTOMATIC,
        skipMemory: Boolean = false
    ) {
        if (this == null) return
        if (centerCrop) {
            Glide.with(this)
                .load(url)
                .placeholder(placeholder)
                .diskCacheStrategy(diskCacheStrategy)
                .error(placeholder)
                .centerCrop()
                .skipMemoryCache(skipMemory)
                .apply(requestOption)
                .into(this)
        } else {
            Glide.with(this)
                .load(url)
                .placeholder(placeholder)
                .diskCacheStrategy(diskCacheStrategy)
                .error(placeholder)
                .skipMemoryCache(skipMemory)
                .apply(requestOption)
                .into(this)
        }
    }
}
