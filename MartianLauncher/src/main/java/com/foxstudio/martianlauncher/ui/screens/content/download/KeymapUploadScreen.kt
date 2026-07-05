package com.foxstudio.martianlauncher.ui.screens.content.download

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.foxstudio.martianlauncher.R
import com.foxstudio.martianlauncher.community.KeymapHubApi
import com.foxstudio.martianlauncher.game.control.zalith.ZalithControlManager
import com.foxstudio.martianlauncher.game.control.zalith.toZalithControls
import com.foxstudio.layer_controller.layout.loadLayoutFromFile
import com.foxstudio.martianlauncher.path.PathManager
import com.foxstudio.martianlauncher.ui.theme.cardColor
import com.foxstudio.martianlauncher.ui.theme.onCardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private data class LayoutEntry(val name: String, val file: File)

@Composable
fun KeymapUploadDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var allLayouts by remember { mutableStateOf<List<LayoutEntry>>(emptyList()) }
    var selectedEntry by remember { mutableStateOf<LayoutEntry?>(null) }
    var layoutContent by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var name by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("1.0.0") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var uploading by remember { mutableStateOf(false) }
    var uploadResult by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val layouts = mutableListOf<LayoutEntry>()
            val dir1 = PathManager.DIR_CONTROL_LAYOUTS
            if (dir1.exists()) {
                dir1.listFiles { f -> f.extension == "json" }?.forEach { file ->
                    layouts.add(LayoutEntry(file.nameWithoutExtension, file))
                }
            }
            val dir2 = ZalithControlManager.layoutDir
            if (dir2.exists()) {
                dir2.listFiles { f -> f.extension == "json" }?.forEach { file ->
                    val entryName = "zalith/${file.nameWithoutExtension}"
                    if (layouts.none { it.name == entryName }) {
                        layouts.add(LayoutEntry(entryName, file))
                    }
                }
            }
            layouts.sortBy { it.name }
            withContext(Dispatchers.Main) {
                allLayouts = layouts
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch(Dispatchers.IO) {
            try {
                val tempFile = File(context.cacheDir, "upload_image.${getExtension(uri.toString())}")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                withContext(Dispatchers.Main) {
                    imageFile = tempFile
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to pick image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.6f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = 6.dp)
                    .fillMaxWidth()
                    .heightIn(max = maxHeight - 12.dp)
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                color = cardColor(false),
                contentColor = onCardColor(),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Upload Layout",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box {
                                FilledTonalButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = selectedEntry?.name ?: "Select a layout",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    shape = MaterialTheme.shapes.large,
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    allLayouts.forEach { entry ->
                                        DropdownMenuItem(
                                            text = { Text(entry.name) },
                                            onClick = {
                                                expanded = false
                                                scope.launch(Dispatchers.IO) {
                                                    try {
                                                        val text = entry.file.readText()
                                                        val content = if (runCatching { ZalithControlManager.parse(text) }.isSuccess) {
                                                            text
                                                        } else {
                                                            val dm = context.resources.displayMetrics
                                                            val martianLayout = loadLayoutFromFile(entry.file)
                                                            val zl = martianLayout.toZalithControls(
                                                                screenWidthPx = dm.widthPixels,
                                                                screenHeightPx = dm.heightPixels,
                                                                density = dm.density
                                                            )
                                                            ZalithControlManager.toJson(zl)
                                                        }
                                                        withContext(Dispatchers.Main) {
                                                            selectedEntry = entry
                                                            layoutContent = content
                                                            if (name.isBlank()) name = entry.name
                                                        }
                                                    } catch (e: Exception) {
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                    if (allLayouts.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No layouts found") },
                                            onClick = { expanded = false }
                                        )
                                    }
                                }
                            }

                            if (layoutContent.isNotBlank()) {
                                Text(
                                    text = "JSON loaded (${layoutContent.length} chars)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = version,
                                onValueChange = { version = it },
                                label = { Text("Version") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = author,
                                onValueChange = { author = it },
                                label = { Text("Author") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }

                        Column(
                            modifier = Modifier.width(96.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { imagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (imageFile != null) "Image" else "Pick",
                                    maxLines = 1
                                )
                            }

                            if (imageFile != null) {
                                FilledTonalButton(
                                    onClick = {
                                        imageFile?.delete()
                                        imageFile = null
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Clear", maxLines = 1)
                                }
                            }

                            Button(
                                onClick = {
                                    if (name.isBlank()) {
                                        Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (layoutContent.isBlank()) {
                                        Toast.makeText(context, "Please select a layout", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    uploading = true
                                    uploadResult = null
                                    scope.launch(Dispatchers.IO) {
                                        runCatching {
                                            val jsonObj = com.google.gson.JsonParser.parseString(layoutContent).asJsonObject
                                            if (name.isNotBlank()) jsonObj.addProperty("name", name)
                                            if (author.isNotBlank()) jsonObj.addProperty("author", author)
                                            if (version.isNotBlank()) jsonObj.addProperty("versionName", version)
                                            val finalJson = jsonObj.toString()
                                            KeymapHubApi.upload(
                                                name = name,
                                                jsonData = finalJson,
                                                version = version,
                                                author = author,
                                                description = description,
                                                imageFile = imageFile
                                            )
                                        }.onSuccess { item ->
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Uploaded: ${item.name}", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            }
                                        }.onFailure { e ->
                                            withContext(Dispatchers.Main) {
                                                uploading = false
                                                uploadResult = "Failed: ${e.message}"
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uploading
                            ) {
                                if (uploading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(stringResource(R.string.community_upload))
                                }
                            }

                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !uploading
                            ) {
                                Text(stringResource(R.string.generic_cancel))
                            }
                        }
                    }

                    if (uploadResult != null) {
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = uploadResult!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uploadResult!!.startsWith("Failed"))
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private fun getExtension(path: String): String {
    val dot = path.lastIndexOf('.')
    return if (dot >= 0) path.substring(dot + 1) else "png"
}
