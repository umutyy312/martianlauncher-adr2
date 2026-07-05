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

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function

/**
 * Evaluates ZalithLauncher's dynamic position expressions (dynamicX / dynamicY).
 *
 * Mirrors net.kdt.pojavlaunch.customcontrols.ControlData#insertDynamicPos:
 *  1. Substitute the ${variable} tokens with concrete pixel values.
 *  2. Evaluate the remaining math with exp4j (with dp()/px() helper functions).
 *
 * All values are in pixels. dp<->px conversion uses the supplied [density].
 */
object ZalithPositionEvaluator {

    /**
     * @param expression the dynamicX or dynamicY string
     * @param buttonWidthPx button width in px
     * @param buttonHeightPx button height in px
     * @param screenWidthPx physical screen width in px
     * @param screenHeightPx physical screen height in px
     * @param density display density (px per dp)
     * @param preferredScale AllSettings button scale equivalent (default 1)
     * @return the evaluated coordinate in px, or 0 on error
     */
    fun evaluate(
        expression: String?,
        buttonWidthPx: Float,
        buttonHeightPx: Float,
        screenWidthPx: Int,
        screenHeightPx: Int,
        density: Float,
        preferredScale: Float = 1f
    ): Float {
        if (expression.isNullOrBlank()) return 0f

        val marginPx = 2f * density //Zalith ControlInterface.getMarginDistance() == dpToPx(2)

        val valueMap = mapOf(
            "top" to "0",
            "left" to "0",
            "right" to (screenWidthPx - buttonWidthPx).toString(),
            "bottom" to (screenHeightPx - buttonHeightPx).toString(),
            "width" to buttonWidthPx.toString(),
            "height" to buttonHeightPx.toString(),
            "screen_width" to screenWidthPx.toString(),
            "screen_height" to screenHeightPx.toString(),
            "margin" to marginPx.toInt().toString(),
            "preferred_scale" to preferredScale.toString()
        )

        var inserted: String = expression.orEmpty()
        for ((key, value) in valueMap) {
            inserted = inserted.replace("\${$key}", value)
        }

        return try {
            ExpressionBuilder(inserted)
                .function(object : Function("dp", 1) {
                    override fun apply(vararg args: Double): Double = args[0] / density //px -> dp
                })
                .function(object : Function("px", 1) {
                    override fun apply(vararg args: Double): Double = args[0] * density //dp -> px
                })
                .build()
                .evaluate()
                .toFloat()
        } catch (e: Exception) {
            0f
        }
    }
}
