package org.ligi.ipfsdroid

import android.content.Context
import android.os.Handler
import android.widget.Toast
import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import io.ipfs.kotlin.IPFS
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DownloadPhotoJob(private val context: Context,
                       private val ipfs: IPFS,
                       private val photoHash: String,
                       private val uiHandler: Handler,
                       private val handler: (success: Boolean) -> Unit)
    : Job(Params(1)) {

    private val TAG = DownloadPhotoJob::class.java.name

    private val okClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        okClient = builder.build()
    }

    private fun showToast(msg: String) {
//        val context = applicationContext
        val duration = Toast.LENGTH_SHORT

        uiHandler.post({
            val toast = Toast.makeText(context, msg, duration)
            toast.show()
        })
    }

    override fun onAdded() {
        // nothing special to do when job is added
    }

    override fun onRun() {

        val gatewayPhotoUrl = "https://gateway.ipfs.io/ipfs/$photoHash"
        showToast("Trying download $gatewayPhotoUrl")

        val tempFile = File.createTempFile("temp_photo_" + photoHash, "jpg", context.cacheDir)

        val request = Request.Builder()
                .url(gatewayPhotoUrl)
                .build()

        val response = okClient.newCall(request).execute().body()!!

        response.use { body ->
            val inputStream = body.byteStream()
            inputStream.use { photoContentStream ->
                showToast("Got response for " + photoHash)
                tempFile.outputStream().use { outputStream ->
                    photoContentStream.copyTo(outputStream)
                }
            }
        }

//        ipfs.get.catStream(photoHash, { photoContentStream ->
//            showToast("Got response for " + photoHash)
//            tempFile.outputStream().use { outputStream ->
//                photoContentStream.copyTo(outputStream)
//            }
//        })

        val photosDir = File(context.filesDir, "photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }

        val finalFile = File(photosDir, photoHash + ".jpg")
        tempFile.renameTo(finalFile)

        handler(true)
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        showToast("Fail $runCount times to download $photoHash")

        var retry = RetryConstraint(true)
        retry.newDelayInMs = 4000
        return retry
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        // nothing special to do when canceling
        showToast("Cancel download " + photoHash)
    }
}