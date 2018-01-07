package org.ligi.ipfsdroid

import android.content.Context
import android.os.AsyncTask
import io.ipfs.kotlin.IPFS

class PhotoDownloader(private val context: Context) {

    fun download(ipfs: IPFS, photoHash: String, handler: (content: String) -> Unit) {
        class AsyncDownloader : AsyncTask<String, Integer, String>() {
            override fun doInBackground(vararg params: String?): String {


                val photoContent: String = ipfs.get.cat("QmZ98g31Wm3ypmPu14rj52Zz3jF457neEwZPKWdrtUWbLA")
                return photoContent
            }

            override fun onPostExecute(result: String?) {
                handler(result!!)
            }
        }

//        val fileOutputStream = context.openFileOutput(photoHash, Context.MODE_PRIVATE)
        AsyncDownloader().execute()
    }
}