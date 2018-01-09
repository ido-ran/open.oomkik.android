package org.ligi.ipfsdroid

import android.content.Context
import android.os.AsyncTask
import io.ipfs.kotlin.IPFS
import java.io.File

class PhotoDownloader(private val context: Context) {

    fun download(ipfs: IPFS, photoHash: String, handler: (success: Boolean) -> Unit) {
        class AsyncDownloader : AsyncTask<String, Integer, Boolean>() {
            override fun doInBackground(vararg params: String?): Boolean {

                try {

                    val tempFile = File.createTempFile("temp_photo_" + photoHash, "jpg", context.cacheDir)

                    ipfs.get.catStream("QmZ98g31Wm3ypmPu14rj52Zz3jF457neEwZPKWdrtUWbLA", { photoContentStream ->
                        tempFile.outputStream().use { outputStream ->
                            photoContentStream.copyTo(outputStream)
                        }
                    })

                    val photosDir = File(context.filesDir, "photos")
                    if (!photosDir.exists()) {
                        photosDir.mkdirs()
                    }

                    val finalFile = File(photosDir, photoHash + ".jpg")
                    tempFile.renameTo(finalFile)

                    return true
                } catch (ex: Exception) {
                    // swallow the exception
                    return false
                }
            }

            override fun onPostExecute(result: Boolean) {
                handler(result)
            }
        }

//        val fileOutputStream = context.openFileOutput(photoHash, Context.MODE_PRIVATE)
        AsyncDownloader().execute()
    }
}