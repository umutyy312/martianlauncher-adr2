package com.foxstudio.martianlauncher.community

import com.foxstudio.martianlauncher.community.model.KeymapItem
import java.io.File
import com.foxstudio.martianlauncher.community.model.KeymapListResponse
import com.foxstudio.martianlauncher.path.GLOBAL_CLIENT
import com.foxstudio.martianlauncher.path.GLOBAL_JSON
import com.foxstudio.martianlauncher.path.TIME_OUT
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json

const val KEYMAPHUB_BASE = "https://datamartian.vercel.app/api"

private val keymapClient = HttpClient(CIO) {
    install(HttpTimeout) { requestTimeoutMillis = TIME_OUT }
    install(ContentNegotiation) { json(GLOBAL_JSON) }
    install(HttpCookies)
    expectSuccess = false
    defaultRequest {
        header(HttpHeaders.UserAgent, "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36")
        header(HttpHeaders.Accept, "application/json, text/plain, */*")
        header("X-Requested-With", "XMLHttpRequest")
    }
}

object KeymapHubApi {

    suspend fun list(
        page: Int = 1,
        limit: Int = 20,
        search: String = ""
    ): KeymapListResponse {
        val response = keymapClient.get("$KEYMAPHUB_BASE/products") {
            parameter("page", page)
            parameter("limit", limit)
            if (search.isNotBlank()) parameter("search", search)
        }
        val text = response.bodyAsText()
        android.util.Log.d("KeymapHub", "list response [${response.status}]: ${text.take(300)}")
        if (text.trimStart().startsWith('<')) {
            error("Server trả HTML thay vì JSON. Kiểm tra lại kết nối API.")
        }
        return GLOBAL_JSON.decodeFromString(text)
    }

    suspend fun getItem(id: Int): KeymapItem {
        val response = keymapClient.get("$KEYMAPHUB_BASE/products/$id")
        val text = response.bodyAsText()
        if (text.trimStart().startsWith('<')) {
            error("Server trả HTML thay vì JSON.")
        }
        return GLOBAL_JSON.decodeFromString(text)
    }

    fun imageUrl(item: KeymapItem): String? {
        val path = item.imagePath ?: return null
        return if (path.startsWith("http")) path else "https://datamartian.vercel.app/$path"
    }

    fun jsonFileUrl(item: KeymapItem): String? {
        val path = item.jsonFile ?: return null
        return if (path.startsWith("http")) path else "https://datamartian.vercel.app/$path"
    }

    suspend fun fetchJsonContent(item: KeymapItem): String {
        return when {
            !item.jsonData.isNullOrBlank() -> item.jsonData
            !item.jsonFile.isNullOrBlank() -> {
                val url = jsonFileUrl(item)!!
                val resp = keymapClient.get(url)
                resp.bodyAsText()
            }
            else -> error("No JSON data available for this keymap")
        }
    }

    suspend fun upload(
        name: String,
        jsonData: String,
        version: String = "1.0.0",
        author: String = "",
        description: String = "",
        imageFile: java.io.File? = null
    ): KeymapItem {
        val response = keymapClient.submitFormWithBinaryData(
            url = "$KEYMAPHUB_BASE/products",
            formData = formData {
                append("name", name)
                append("version", version)
                append("author", author)
                append("description", description)
                append("json_data", jsonData)
                append("json_file", jsonData.encodeToByteArray(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"${name}.json\"")
                })
                if (imageFile != null) {
                    val imageBytes = imageFile.readBytes()
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"${imageFile.name}\"")
                    })
                }
            }
        ) {
            method = HttpMethod.Post
        }
        val text = response.bodyAsText()
        android.util.Log.d("KeymapHub", "upload response [${response.status}]: ${text.take(500)}")
        if (text.trimStart().startsWith('<')) {
            error("Server returned HTML instead of JSON.")
        }
        if (!response.status.value.toString().startsWith("2")) {
            error("Server error(${response.status}: '${text.take(200)}')")
        }
        return GLOBAL_JSON.decodeFromString(text)
    }
}
