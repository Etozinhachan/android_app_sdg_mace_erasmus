package me.erasmusteam.odsmaceerasmusapp.requests

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException

class MultipartRequest(
    url: String,
    private val headers: Map<String, String> = emptyMap(),
    private val jsonPart: String, // JSON data
    private val files: List<ByteArray>, // List of image data
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(Method.POST, url, errorListener) {

    private val boundary = "Boundary-${System.currentTimeMillis()}"
    private val lineEnd = "\r\n"
    private val twoHyphens = "--"

    override fun getHeaders(): Map<String, String> = headers

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    override fun getBody(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(outputStream)

        try {
            // Add JSON part
            dataOutputStream.writeBytes("$twoHyphens$boundary$lineEnd")
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"json\"$lineEnd")
            dataOutputStream.writeBytes("Content-Type: application/json; charset=UTF-8$lineEnd$lineEnd")
            dataOutputStream.writeBytes("$jsonPart$lineEnd")

            // Add files
            files.forEachIndexed { index, fileData ->
                dataOutputStream.writeBytes("$twoHyphens$boundary$lineEnd")
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file$index\"; filename=\"file$index.webp\"$lineEnd")
                dataOutputStream.writeBytes("Content-Type: image/webp$lineEnd$lineEnd")
                dataOutputStream.write(fileData)
                dataOutputStream.writeBytes(lineEnd)
            }

            // End multipart
            dataOutputStream.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dataOutputStream.flush()
                dataOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return outputStream.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        return try {
            val responseString = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers, "utf-8")))
            Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(VolleyError("Unsupported Encoding Exception", e))
        }
    }

    override fun deliverResponse(response: String) {
        listener.onResponse(response)
    }
}
