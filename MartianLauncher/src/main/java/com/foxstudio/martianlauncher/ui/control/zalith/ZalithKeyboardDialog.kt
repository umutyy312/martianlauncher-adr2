package com.foxstudio.martianlauncher.ui.control.zalith

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.foxstudio.martianlauncher.game.keycodes.LwjglGlfwKeycode
import com.foxstudio.martianlauncher.game.keycodes.zalith.ZalithControlEventKeycode
import java.lang.reflect.Modifier as JavaModifier

private data class KeyEntry(val code: Int, val label: String)

private val GLFW_ENTRIES: List<KeyEntry> by lazy {
    LwjglGlfwKeycode::class.java.fields
        .filter { field ->
            JavaModifier.isPublic(field.modifiers) &&
            JavaModifier.isStatic(field.modifiers) &&
            JavaModifier.isFinal(field.modifiers) &&
            (field.type == Short::class.java || field.type == Short::class.javaPrimitiveType)
        }
        .mapNotNull { field ->
            val code = (field.get(null) as? Short)?.toInt() ?: return@mapNotNull null
            if (code <= 0) return@mapNotNull null
            KeyEntry(code, field.name.removePrefix("GLFW_KEY_").replace("_", " "))
        }
        .sortedBy { it.code }
}

private val SPECIAL_ENTRIES: List<KeyEntry> = listOf(
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_KEYBOARD,    "Keyboard (IME)"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_TOGGLECTRL,  "GUI / Toggle Controls"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_MOUSEPRI,    "Mouse Left Click"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_MOUSESEC,    "Mouse Right Click"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_MOUSEMID,    "Mouse Middle Click"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_VIRTUALMOUSE,"Virtual Mouse"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_SCROLLUP,    "Scroll Up"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_SCROLLDOWN,  "Scroll Down"),
    KeyEntry(ZalithControlEventKeycode.SPECIALBTN_MENU,        "Menu"),
)

@Composable
fun ZalithKeyboardDialog(
    onDismissRequest: () -> Unit,
    onKeySelected: (keycode: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var tab by remember { mutableStateOf(0) }
    val entries = if (tab == 0) SPECIAL_ENTRIES else GLFW_ENTRIES

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Select Key",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Special", "GLFW Keys").forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (tab == index) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .clickable { tab = index }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (tab == index)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(entries, key = { it.code }) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onKeySelected(entry.code) }
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = entry.code.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
