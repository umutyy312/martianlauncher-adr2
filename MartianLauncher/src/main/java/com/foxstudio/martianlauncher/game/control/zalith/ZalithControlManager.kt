/*
 * Martian Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.foxstudio.martianlauncher.game.control.zalith

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLCustomControls
import com.foxstudio.martianlauncher.path.PathManager
import java.io.File
import java.io.InputStream
import java.util.UUID

/**
 * Loads, imports, lists and deletes ZalithLauncher-format control layouts.
 * Completely independent of MartianLauncher's own ControlManager: data lives in
 * a separate directory and never touches the native control layout files.
 */
object ZalithControlManager {

    private val gson = Gson()

    /** Separate storage dir so Zalith layouts never mix with Martian ones. */
    val layoutDir: File by lazy {
        File(PathManager.DIR_FILES_EXTERNAL, "zalith_control_layouts").apply { mkdirs() }
    }

    /**
     * Parse a Zalith control JSON string into the data model.
     * Supports version 8 directly; older versions are parsed best-effort
     * (Gson tolerates missing/extra fields).
     *
     * @throws Exception if the JSON is not a valid Zalith layout
     */
    fun parse(jsonString: String): ZLCustomControls {
        val root: JsonObject = JsonParser.parseString(jsonString).asJsonObject
        //A real Zalith layout always has mControlDataList
        if (!root.has("mControlDataList")) {
            error("Not a ZalithLauncher control layout (missing mControlDataList)")
        }
        val result = gson.fromJson(jsonString, ZLCustomControls::class.java)
        if (root.has("name")) result.name = root.get("name").asString
        if (root.has("author")) result.author = root.get("author").asString
        if (root.has("versionName")) result.versionName = root.get("versionName").asString
        return result
    }

    /**
     * Import a Zalith control layout from a stream, validating it parses,
     * then persist it into [layoutDir].
     * @return the saved file
     */
    fun import(inputStream: InputStream, name: String? = null): File {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        //Validate before saving
        parse(jsonString)
        val safeName = name?.takeIf { it.isNotBlank() }
            ?.replace(Regex("""[\\/:*?"<>|]"""), "_")
            ?: UUID.randomUUID().toString()
        val file = File(layoutDir, "${safeName}.json")
        var finalFile = file
        var counter = 1
        while (finalFile.exists()) {
            finalFile = File(layoutDir, "${safeName}_${counter}.json")
            counter++
        }
        finalFile.writeText(jsonString)
        return finalFile
    }

    /** List every imported Zalith layout file. */
    fun list(): List<File> {
        return layoutDir.listFiles { f -> f.isFile && f.extension == "json" }
            ?.sortedBy { it.name }
            ?: emptyList()
    }

    /** Load and parse a specific layout file. */
    fun load(file: File): ZLCustomControls {
        return parse(file.readText())
    }

    fun delete(file: File): Boolean {
        return file.delete()
    }

    fun saveLayout(controls: ZLCustomControls, file: File) {
        file.writeText(gson.toJson(controls))
    }

    fun toJson(controls: ZLCustomControls): String {
        return gson.toJson(controls)
    }
}
