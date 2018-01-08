package org.ligi.ipfsdroid

import android.content.Context
import android.os.AsyncTask
import io.ipfs.kotlin.IPFS
import java.io.File
import java.io.InputStream

class PhotoDownloader(private val context: Context) {

    fun download(ipfs: IPFS, photoHash: String, handler: (content: String) -> Unit) {
        class AsyncDownloader : AsyncTask<String, Integer, String>() {
            override fun doInBackground(vararg params: String?): String{


                val tempFile = File.createTempFile("temp_photo_" + photoHash, "jpg", context.cacheDir)

                ipfs.get.catStream("QmZ98g31Wm3ypmPu14rj52Zz3jF457neEwZPKWdrtUWbLA", { photoContentStream ->
                    tempFile.outputStream().use { outputStream ->
                        photoContentStream.copyTo(outputStream)
                    }
                })

                val finalFile = File(context.filesDir, photoHash + ".jpg")
                tempFile.renameTo(finalFile)

                return finalFile.absolutePath
            }

            override fun onPostExecute(result: String) {
                handler(result!!)
            }
        }

//        val fileOutputStream = context.openFileOutput(photoHash, Context.MODE_PRIVATE)
        AsyncDownloader().execute()
    }
}