package me.erasmusteam.odsmaceerasmusapp.requests

import android.graphics.BitmapFactory
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import me.erasmusteam.odsmaceerasmusapp.data.ActivityData

class MultipartResponseRequest(
    url: String,
    private val headers: Map<String, String> = emptyMap(),
    private val listener: Response.Listener<List<ActivityData>>,
    errorListener: Response.ErrorListener
) : Request<List<ActivityData>>(Method.GET, url, errorListener) {

    override fun getHeaders(): Map<String, String> = headers

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<ActivityData>> {
        return try {
            //val rawResponse = String(response.data, Charsets.UTF_8)
            //println("Raw Response: $rawResponse") // Log raw response
            val boundary = extractBoundary(response.headers?.get("Content-Type"))
            val activities = parseMultipartResponse(response.data, boundary)
            Response.success(activities, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            println("Error Parsing Response: ${e.message}")
            Response.error(VolleyError("Failed to parse response", e))
        }
    }


    override fun deliverResponse(response: List<ActivityData>) {
        listener.onResponse(response)
    }

    private fun extractBoundary(contentType: String?): String {
        return contentType?.substringAfter("boundary=")?.replace("\"", "")?.trim()
            ?: throw IllegalArgumentException("Boundary not found in Content-Type header")
    }



    private fun parseMultipartResponse(data: ByteArray, boundary: String): List<ActivityData> {
        val activities = mutableListOf<ActivityData>()
        val parts = splitMultipartData(data, boundary)

        //println("GRRR: ${parts}")

        var currentActivity: ActivityData? = null

        for (part in parts) {

           // print("meow")

            val (headers, body) = extractHeadersAndBody(part) ?: continue

            //println("Arroiz: ${headers}")

            if (headers.contains("application/json")) {
                val jsonString = body.toString(Charsets.UTF_8).trim()
                println("Extracted JSON: $jsonString")

                try {
                    val tempData = Gson().fromJson<Map<String, Any>>(jsonString, Map::class.java)

                    //println("test activity_state: ${tempData["activity_state"]?.toString()}")

                    //println("test map: ${tempData}")

                    currentActivity = ActivityData(
                        id = tempData["id"]?.toString() ?: "",
                        activity_state = tempData["activity_state"]?.toString()?.toDoubleOrNull()?.toInt() ?: 0,
                        country = tempData["country"]?.toString() ?: "",
                        explanation = tempData["explanation"]?.toString() ?: "",
                        latitude = tempData["latitude"]?.toString()?.toDoubleOrNull() ?: 0.0,
                        longitude = tempData["longitude"]?.toString()?.toDoubleOrNull() ?: 0.0,
                        ods = tempData["ods"]?.toString() ?: "",
                        type = tempData["type"]?.toString() ?: "",
                        user_id = tempData["user_id"]?.toString() ?: "",
                        image_types = mutableListOf(),
                        images = mutableListOf()
                    )
                    activities.add(currentActivity)
                } catch (e: Exception) {
                    println("Error parsing JSON: $e")
                }
            } else if (headers.contains("image/")) {
                //println("Extracted Image: Size=${body.size} bytes")
                currentActivity?.image_types?.add(headers.substringAfter("image/"))
                currentActivity?.images?.add(body) ?: println("Warning: Image without JSON context.")
                if (checkImgCorrupted(body)){
                    println("Imagem corrompida")
                    println("Imagem body used: ${String(body, Charsets.UTF_8)}")
                }
            } else {
                println("Unknown part: Headers=$headers")
            }
        }

        return activities
    }

    fun checkImgCorrupted(byteArray: ByteArray): Boolean {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
        return options.outWidth == -1 || options.outHeight == -1
    }


    private fun extractHeadersAndBody(part: ByteArray): Pair<String, ByteArray>? {
        val separator = "\r\n\r\n".toByteArray(Charsets.UTF_8)
        val splitIndex = part.indexOf(separator)
        if (splitIndex == -1) {
            println("Malformed part: no header-body separator found.")
            return null
        }
        // Headers: convert to string and trim (they are text)
        val headers = part.sliceArray(0 until splitIndex).toString(Charsets.UTF_8).trim()
        // Body: leave as raw bytes (do NOT convert to string and trim)
        val body = part.sliceArray(splitIndex + separator.size until part.size)
        return headers to body
    }


    private fun splitByteArray(data: ByteArray, boundary: ByteArray): List<ByteArray> {
        val parts = mutableListOf<ByteArray>()
        var start = 0

        while (start < data.size) {
            val index = data.indexOf(boundary, start)
            if (index == -1) break // No more boundaries

            val part = data.sliceArray(start until index).trimPart()
            if (part.isNotEmpty()) parts.add(part)

            start = index + boundary.size
        }

        return parts
    }

    private fun splitMultipartData(data: ByteArray, boundary: String): List<ByteArray> {
        // The boundary in the raw response appears as: "--boundary_xxxx" followed by CRLF.
        val boundaryMarker = "--$boundary".toByteArray(Charsets.UTF_8)
        val crlf = "\r\n".toByteArray(Charsets.UTF_8)
        val fullBoundary = boundaryMarker + crlf  // e.g. "--boundary_xxxx\r\n"

        val parts = mutableListOf<ByteArray>()
        var start = 0

        while (true) {
            // Find the next occurrence of the full boundary
            val index = data.indexOf(fullBoundary, start)
            if (index == -1) break  // no more boundaries

            // Move start to after this boundary marker
            var partStart = index + fullBoundary.size

            // Find the next boundary marker from here
            val nextIndex = data.indexOf(fullBoundary, partStart)
            if (nextIndex == -1) {
                // This should be the last part (or the end boundary, which ends with "--")
                // Optionally, you can look for the end boundary marker.
                if (partStart < data.size) {
                    parts.add(data.sliceArray(partStart until data.size))
                }
                break
            } else {
                val part = data.sliceArray(partStart until nextIndex)
                if (part.isNotEmpty()) parts.add(part)
                start = nextIndex
            }
        }
        return parts
    }


    // Helper function to trim unnecessary newlines and spaces
    private fun ByteArray.trimPart(): ByteArray {
        return this.toString(Charsets.UTF_8).trim().toByteArray(Charsets.UTF_8)
    }



    private fun ByteArray.indexOf(subArray: ByteArray, startIndex: Int = 0): Int {
        for (i in startIndex..(this.size - subArray.size)) {
            if (this.sliceArray(i until i + subArray.size).contentEquals(subArray)) {
                return i
            }
        }
        return -1
    }






}
