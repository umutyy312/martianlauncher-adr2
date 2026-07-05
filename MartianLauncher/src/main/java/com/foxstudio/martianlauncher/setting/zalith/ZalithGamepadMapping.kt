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

package com.foxstudio.martianlauncher.setting.zalith

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZalithGamepadMapping(
    @SerialName("BUTTON_A")
    val BUTTON_A: IntArray = intArrayOf(-99),
    @SerialName("BUTTON_B")
    val BUTTON_B: IntArray = intArrayOf(-99),
    @SerialName("BUTTON_X")
    val BUTTON_X: IntArray = intArrayOf(-99),
    @SerialName("BUTTON_Y")
    val BUTTON_Y: IntArray = intArrayOf(-99),
    @SerialName("BUTTON_START")
    val BUTTON_START: IntArray = intArrayOf(-99),
    @SerialName("BUTTON_SELECT")
    val BUTTON_SELECT: IntArray = intArrayOf(-99),
    @SerialName("TRIGGER_LEFT")
    val TRIGGER_LEFT: IntArray = intArrayOf(-99),
    @SerialName("TRIGGER_RIGHT")
    val TRIGGER_RIGHT: IntArray = intArrayOf(-99),
    @SerialName("SHOULDER_LEFT")
    val SHOULDER_LEFT: IntArray = intArrayOf(-99),
    @SerialName("SHOULDER_RIGHT")
    val SHOULDER_RIGHT: IntArray = intArrayOf(-99),
    @SerialName("THUMBSTICK_LEFT")
    val THUMBSTICK_LEFT: IntArray = intArrayOf(-99),
    @SerialName("THUMBSTICK_RIGHT")
    val THUMBSTICK_RIGHT: IntArray = intArrayOf(-99),
    @SerialName("DPAD_UP")
    val DPAD_UP: IntArray = intArrayOf(-99),
    @SerialName("DPAD_DOWN")
    val DPAD_DOWN: IntArray = intArrayOf(-99),
    @SerialName("DPAD_LEFT")
    val DPAD_LEFT: IntArray = intArrayOf(-99),
    @SerialName("DPAD_RIGHT")
    val DPAD_RIGHT: IntArray = intArrayOf(-99),
    @SerialName("DIRECTION_FORWARD")
    val DIRECTION_FORWARD: IntArray = intArrayOf(-99),
    @SerialName("DIRECTION_BACKWARD")
    val DIRECTION_BACKWARD: IntArray = intArrayOf(-99),
    @SerialName("DIRECTION_LEFT")
    val DIRECTION_LEFT: IntArray = intArrayOf(-99),
    @SerialName("DIRECTION_RIGHT")
    val DIRECTION_RIGHT: IntArray = intArrayOf(-99),
    @SerialName("inGame")
    val inGame: Boolean = true
) {
    companion object {
        const val MOUSE_SCROLL_DOWN = -1
        const val MOUSE_SCROLL_UP = -2
        const val MOUSE_LEFT = -3
        const val MOUSE_MIDDLE = -4
        const val MOUSE_RIGHT = -5
        const val UNSPECIFIED = -6

        fun getDefaultGameMap(): ZalithGamepadMapping {
            return ZalithGamepadMapping(
                BUTTON_A = intArrayOf(57),
                BUTTON_X = intArrayOf(29),
                DPAD_UP = intArrayOf(17),
                DPAD_DOWN = intArrayOf(31),
                DPAD_LEFT = intArrayOf(30),
                DPAD_RIGHT = intArrayOf(32),
                inGame = true
            )
        }

        fun getDefaultMenuMap(): ZalithGamepadMapping {
            return ZalithGamepadMapping(
                BUTTON_A = intArrayOf(57),
                DPAD_UP = intArrayOf(17),
                DPAD_DOWN = intArrayOf(31),
                DPAD_LEFT = intArrayOf(30),
                DPAD_RIGHT = intArrayOf(32),
                inGame = false
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZalithGamepadMapping

        if (!BUTTON_A.contentEquals(other.BUTTON_A)) return false
        if (!BUTTON_B.contentEquals(other.BUTTON_B)) return false
        if (!BUTTON_X.contentEquals(other.BUTTON_X)) return false
        if (!BUTTON_Y.contentEquals(other.BUTTON_Y)) return false
        if (!BUTTON_START.contentEquals(other.BUTTON_START)) return false
        if (!BUTTON_SELECT.contentEquals(other.BUTTON_SELECT)) return false
        if (!TRIGGER_LEFT.contentEquals(other.TRIGGER_LEFT)) return false
        if (!TRIGGER_RIGHT.contentEquals(other.TRIGGER_RIGHT)) return false
        if (!SHOULDER_LEFT.contentEquals(other.SHOULDER_LEFT)) return false
        if (!SHOULDER_RIGHT.contentEquals(other.SHOULDER_RIGHT)) return false
        if (!THUMBSTICK_LEFT.contentEquals(other.THUMBSTICK_LEFT)) return false
        if (!THUMBSTICK_RIGHT.contentEquals(other.THUMBSTICK_RIGHT)) return false
        if (!DPAD_UP.contentEquals(other.DPAD_UP)) return false
        if (!DPAD_DOWN.contentEquals(other.DPAD_DOWN)) return false
        if (!DPAD_LEFT.contentEquals(other.DPAD_LEFT)) return false
        if (!DPAD_RIGHT.contentEquals(other.DPAD_RIGHT)) return false
        if (!DIRECTION_FORWARD.contentEquals(other.DIRECTION_FORWARD)) return false
        if (!DIRECTION_BACKWARD.contentEquals(other.DIRECTION_BACKWARD)) return false
        if (!DIRECTION_LEFT.contentEquals(other.DIRECTION_LEFT)) return false
        if (!DIRECTION_RIGHT.contentEquals(other.DIRECTION_RIGHT)) return false
        if (inGame != other.inGame) return false

        return true
    }

    override fun hashCode(): Int {
        var result = BUTTON_A.contentHashCode()
        result = 31 * result + BUTTON_B.contentHashCode()
        result = 31 * result + BUTTON_X.contentHashCode()
        result = 31 * result + BUTTON_Y.contentHashCode()
        result = 31 * result + BUTTON_START.contentHashCode()
        result = 31 * result + BUTTON_SELECT.contentHashCode()
        result = 31 * result + TRIGGER_LEFT.contentHashCode()
        result = 31 * result + TRIGGER_RIGHT.contentHashCode()
        result = 31 * result + SHOULDER_LEFT.contentHashCode()
        result = 31 * result + SHOULDER_RIGHT.contentHashCode()
        result = 31 * result + THUMBSTICK_LEFT.contentHashCode()
        result = 31 * result + THUMBSTICK_RIGHT.contentHashCode()
        result = 31 * result + DPAD_UP.contentHashCode()
        result = 31 * result + DPAD_DOWN.contentHashCode()
        result = 31 * result + DPAD_LEFT.contentHashCode()
        result = 31 * result + DPAD_RIGHT.contentHashCode()
        result = 31 * result + DIRECTION_FORWARD.contentHashCode()
        result = 31 * result + DIRECTION_BACKWARD.contentHashCode()
        result = 31 * result + DIRECTION_LEFT.contentHashCode()
        result = 31 * result + DIRECTION_RIGHT.contentHashCode()
        result = 31 * result + inGame.hashCode()
        return result
    }
}
