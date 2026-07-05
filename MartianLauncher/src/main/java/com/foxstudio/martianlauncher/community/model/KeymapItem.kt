package com.foxstudio.martianlauncher.community.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeymapItem(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("version") val version: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("json_data") val jsonData: String? = null,
    @SerialName("json_file") val jsonFile: String? = null,
    @SerialName("image_path") val imagePath: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class KeymapListResponse(
    @SerialName("data") val data: List<KeymapItem>,
    @SerialName("pagination") val pagination: Pagination
)

@Serializable
data class Pagination(
    @SerialName("page") val page: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total") val total: Int,
    @SerialName("pages") val pages: Int
)
