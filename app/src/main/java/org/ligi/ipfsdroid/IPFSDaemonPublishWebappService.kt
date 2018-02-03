package org.ligi.ipfsdroid

import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import org.ligi.tracedroid.logging.Log
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Service responsible for publishing the management webapp to IPFS.
 */
class IPFSDaemonPublishWebappService : IntentService("IPFSDaemonPublishWebappService") {

    private var ipfsAdd: Process? = null

    override fun onHandleIntent(intent: Intent) {

        try {
            val webappFolder = filesDir.absolutePath + "/webapp/"
            Decompress.unzipFromAssets(this, "webapp.zip", webappFolder)

            ipfsAdd = IPFSDaemon(baseContext).run("add --recursive --quieter " + webappFolder)
            val exitCode = ipfsAdd!!.waitFor()

            BufferedReader(InputStreamReader(ipfsAdd!!.inputStream)).use { reader ->
                val rootHash = reader.readText()
                Log.d("ipfs add done. " + rootHash)
            }

            BufferedReader(InputStreamReader(ipfsAdd!!.errorStream)).use { reader ->
                val error = reader.readText()
                Log.d("ipfs add error. " + error)
            }

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        ipfsAdd!!.destroy()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (action != null && action == "STOP") {
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

}
