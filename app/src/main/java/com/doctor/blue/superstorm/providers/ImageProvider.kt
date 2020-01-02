package com.doctor.blue.superstorm.providers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import com.doctor.blue.superstorm.R

class ImageProvider(private var context: Context) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg p0: String?): Bitmap? {
        return getAlbumBitmap(p0[0])
    }

    private fun getAlbumBitmap(path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
            val embedPic = retriever.embeddedPicture
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.size)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        return bitmap
    }
}