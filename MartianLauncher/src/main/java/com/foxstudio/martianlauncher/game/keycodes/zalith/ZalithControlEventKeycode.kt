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

package com.foxstudio.martianlauncher.game.keycodes.zalith

import android.content.Context
import com.foxstudio.martianlauncher.R

object ZalithControlEventKeycode {
    const val SPECIALBTN_KEYBOARD = -1
    const val SPECIALBTN_TOGGLECTRL = -2
    const val SPECIALBTN_MOUSEPRI = -3
    const val SPECIALBTN_MOUSESEC = -4
    const val SPECIALBTN_VIRTUALMOUSE = -5
    const val SPECIALBTN_MOUSEMID = -6
    const val SPECIALBTN_SCROLLUP = -7
    const val SPECIALBTN_SCROLLDOWN = -8
    const val SPECIALBTN_MENU = -9

    fun getDisplayName(context: Context, code: Int): String? {
        return when (code) {
            SPECIALBTN_KEYBOARD -> context.getString(R.string.keycode_special_keyboard)
            SPECIALBTN_TOGGLECTRL -> "GUI"
            SPECIALBTN_MOUSEPRI -> context.getString(R.string.keycode_special_pri)
            SPECIALBTN_MOUSESEC -> context.getString(R.string.keycode_special_sec)
            SPECIALBTN_VIRTUALMOUSE -> context.getString(R.string.keycode_special_mouse)
            SPECIALBTN_MOUSEMID -> context.getString(R.string.keycode_special_mid)
            SPECIALBTN_SCROLLUP -> context.getString(R.string.keycode_special_scrollup)
            SPECIALBTN_SCROLLDOWN -> context.getString(R.string.keycode_special_scrolldown)
            SPECIALBTN_MENU -> context.getString(R.string.keycode_special_menu)
            else -> null
        }
    }

    fun buildSpecialButtonArray(context: Context): Array<String> {
        return arrayOf(
            context.getString(R.string.keycode_special_menu),
            context.getString(R.string.keycode_special_scrolldown),
            context.getString(R.string.keycode_special_scrollup),
            context.getString(R.string.keycode_special_mid),
            context.getString(R.string.keycode_special_mouse),
            context.getString(R.string.keycode_special_sec),
            context.getString(R.string.keycode_special_pri),
            "GUI",
            context.getString(R.string.keycode_special_keyboard)
        )
    }

    fun getSpecialKeycodeNames(context: Context): Array<String> {
        return arrayOf(
            context.getString(R.string.keycode_unspecified),
            context.getString(R.string.keycode_mouse_right),
            context.getString(R.string.keycode_mouse_middle),
            context.getString(R.string.keycode_mouse_left),
            context.getString(R.string.keycode_scroll_up),
            context.getString(R.string.keycode_scroll_down)
        )
    }
}
