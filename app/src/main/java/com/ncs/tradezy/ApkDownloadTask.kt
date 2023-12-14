package com.ncs.tradezy

import android.content.Context
import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ApkDownloadTask(
    private val context: Context,
    private val downloadUrl: String,
    private val destinationPath: String,
    private val onProgressUpdate: (Int) -> Unit
) : AsyncTask<Void, Int, File>() {

    override fun doInBackground(vararg params: Void?): File? {
        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // Get the total file size
            val fileLength = connection.contentLength

            // Input stream to read file
            val input = BufferedInputStream(url.openStream(), 8192)

            // Output stream to write file
            val output = FileOutputStream(destinationPath)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()

                // Publishing the progress
                publishProgress((total * 100 / fileLength).toInt())

                output.write(data, 0, count)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()

            return File(destinationPath)
        } catch (e: Exception) {
            // Handle exceptions, you might want to log or notify the user
            return null
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        // Update the progress bar or UI with the download progress
        val progress = values[0] ?: 0
        onProgressUpdate(progress)
    }

    override fun onPostExecute(result: File?) {
        // The download is complete, you can perform any post-download operations here
    }
}
