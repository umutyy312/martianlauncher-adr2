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

package com.foxstudio.martianlauncher.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.foxstudio.layer_controller.layout.ControlLayout
import com.foxstudio.layer_controller.layout.loadLayoutFromFile
import com.foxstudio.martianlauncher.game.control.zalith.ZalithControlManager
import com.foxstudio.martianlauncher.game.control.zalith.toMartianLayout
import com.foxstudio.martianlauncher.game.control.zalith.toZalithControls
import com.foxstudio.martianlauncher.setting.AllSettings
import com.foxstudio.martianlauncher.ui.base.BaseAppCompatActivity
import com.foxstudio.martianlauncher.ui.base.applyFullscreen
import com.foxstudio.martianlauncher.ui.screens.content.elements.Background
import com.foxstudio.martianlauncher.ui.screens.main.control_editor.ControlEditor
import com.foxstudio.martianlauncher.ui.theme.MartianLauncherTheme
import com.foxstudio.martianlauncher.ui.theme.backgroundColor
import com.foxstudio.martianlauncher.ui.theme.onBackgroundColor
import com.foxstudio.martianlauncher.viewmodel.BackgroundViewModel
import com.foxstudio.martianlauncher.viewmodel.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

private const val BUNDLE_CONTROL = "BUNDLE_CONTROL"
private const val BUNDLE_IS_ZALITH = "BUNDLE_IS_ZALITH"

@AndroidEntryPoint
class ControlEditorActivity : BaseAppCompatActivity() {
    private val editorViewModel: EditorViewModel by viewModels()
    private val backgroundViewModel: BackgroundViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val controlPath: String = intent.extras?.getString(BUNDLE_CONTROL) ?: return runFinish()
        val isZalith: Boolean = intent.extras?.getBoolean(BUNDLE_IS_ZALITH, false) ?: false
        val controlFile: File = File(controlPath).takeIf { it.isFile && it.exists() } ?: return runFinish()

        val layout: ControlLayout = if (isZalith) {
            runCatching {
                val zlControls = ZalithControlManager.load(controlFile)
                zlControls.toMartianLayout(
                    name = controlFile.nameWithoutExtension,
                    screenWidthPx = resources.displayMetrics.widthPixels,
                    screenHeightPx = resources.displayMetrics.heightPixels,
                    density = resources.displayMetrics.density
                )
            }.getOrNull() ?: return runFinish()
        } else {
            runCatching { loadLayoutFromFile(controlFile) }.getOrNull() ?: return runFinish()
        }

        editorViewModel.initLayout(layout)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                editorViewModel.onBackPressed(context = this@ControlEditorActivity) {
                    this@ControlEditorActivity.finish()
                }
            }
        })

        setContent {
            MartianLauncherTheme(backgroundViewModel = backgroundViewModel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor(),
                    contentColor = onBackgroundColor()
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.applyFullscreen(AllSettings.launcherFullScreen.state)
                    ) {
                        Background(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = backgroundViewModel,
                            allowVideo = false
                        )

                        ControlEditor(
                            viewModel = editorViewModel,
                            targetFile = controlFile,
                            exit = {
                                if (isZalith) saveZalithLayout(controlFile)
                                finish()
                            },
                            menuExit = {
                                editorViewModel.showExitEditorDialog(
                                    context = this@ControlEditorActivity,
                                    onExit = { finish() }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun saveZalithLayout(originalFile: File) {
        runCatching {
            val martianLayout = editorViewModel.packLayout() ?: return
            val zlControls = martianLayout.toZalithControls(
                screenWidthPx = resources.displayMetrics.widthPixels,
                screenHeightPx = resources.displayMetrics.heightPixels,
                density = resources.displayMetrics.density
            )
            ZalithControlManager.saveLayout(zlControls, originalFile)
        }
    }
}

fun startEditorActivity(context: Context, file: File) {
    val intent = Intent(context, ControlEditorActivity::class.java).apply {
        putExtra(BUNDLE_CONTROL, file.absolutePath)
    }
    context.startActivity(intent)
}

fun startZalithEditorActivity(context: Context, file: File) {
    val intent = Intent(context, ControlEditorActivity::class.java).apply {
        putExtra(BUNDLE_CONTROL, file.absolutePath)
        putExtra(BUNDLE_IS_ZALITH, true)
    }
    context.startActivity(intent)
}