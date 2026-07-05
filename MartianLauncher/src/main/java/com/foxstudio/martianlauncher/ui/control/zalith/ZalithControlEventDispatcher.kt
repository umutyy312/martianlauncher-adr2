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

package com.foxstudio.martianlauncher.ui.control.zalith

import com.foxstudio.martianlauncher.game.keycodes.LwjglGlfwKeycode
import com.foxstudio.martianlauncher.game.keycodes.zalith.ZalithControlEventKeycode
import org.lwjgl.glfw.CallbackBridge

/**
 * Runtime dispatch for ZalithLauncher-format control buttons.
 *
 * Mirrors net.kdt.pojavlaunch.customcontrols.buttons.ControlButton#sendKeyPresses:
 *  - keycode >= 0  -> sent directly as a GLFW keycode (the stored value already IS
 *                     the GLFW keycode, NOT an index into any table).
 *  - keycode <  0  -> a SPECIALBTN_* action (mouse click, scroll, or a launcher
 *                     toggle routed through the callbacks below).
 *
 * The toggle callbacks are set by the game screen while Zalith mode is active.
 */
object ZalithControlEventDispatcher {

    /** Toggle the soft keyboard / IME overlay. */
    var onSwitchKeyboard: () -> Unit = {}
    /** Toggle visibility of the on-screen controls. */
    var onToggleControls: () -> Unit = {}
    /** Toggle the virtual mouse / trackpad mode. */
    var onToggleVirtualMouse: () -> Unit = {}
    /** Open/close the in-game menu overlay. */
    var onSwitchMenu: () -> Unit = {}

    fun sendZalithKeycode(keycode: Int, isPressed: Boolean) {
        when {
            keycode > 0 -> {
                //Direct GLFW keycode
                CallbackBridge.sendKeyPress(keycode, CallbackBridge.getCurrentMods(), isPressed)
                CallbackBridge.setModifiers(keycode, isPressed)
            }
            keycode == 0 -> {
                //GLFW_KEY_UNKNOWN / unused slot: nothing to send
            }
            else -> {
                when (keycode) {
                    ZalithControlEventKeycode.SPECIALBTN_KEYBOARD ->
                        if (isPressed) onSwitchKeyboard()
                    ZalithControlEventKeycode.SPECIALBTN_TOGGLECTRL ->
                        if (isPressed) onToggleControls()
                    ZalithControlEventKeycode.SPECIALBTN_MOUSEPRI ->
                        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT.toInt(), isPressed)
                    ZalithControlEventKeycode.SPECIALBTN_MOUSESEC ->
                        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT.toInt(), isPressed)
                    ZalithControlEventKeycode.SPECIALBTN_VIRTUALMOUSE ->
                        if (isPressed) onToggleVirtualMouse()
                    ZalithControlEventKeycode.SPECIALBTN_MOUSEMID ->
                        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_MIDDLE.toInt(), isPressed)
                    ZalithControlEventKeycode.SPECIALBTN_SCROLLUP ->
                        if (isPressed) CallbackBridge.sendScroll(0.0, 1.0)
                    ZalithControlEventKeycode.SPECIALBTN_SCROLLDOWN ->
                        if (isPressed) CallbackBridge.sendScroll(0.0, -1.0)
                    ZalithControlEventKeycode.SPECIALBTN_MENU ->
                        if (isPressed) onSwitchMenu()
                }
            }
        }
    }
}
