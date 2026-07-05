package com.foxstudio.martianlauncher.game.control.zalith

import androidx.compose.ui.graphics.Color
import com.foxstudio.layer_controller.data.ButtonSize
import com.foxstudio.layer_controller.data.NormalData
import com.foxstudio.layer_controller.data.VisibilityType
import com.foxstudio.layer_controller.data.ButtonStyle
import com.foxstudio.layer_controller.event.ClickEvent
import com.foxstudio.layer_controller.layout.ControlLayout
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLControlData
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLCustomControls
import com.foxstudio.martianlauncher.game.keycodes.ControlEventKeycode
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_DOWN
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_DOWN_SINGLE
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_UP
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_UP_SINGLE
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SWITCH_IME
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SWITCH_MENU

private val EVENT_KEY_TO_GLFW_INT: Map<String, Int> by lazy {
    mapOf(
        "GLFW_KEY_SPACE" to 32, "GLFW_KEY_APOSTROPHE" to 39,
        "GLFW_KEY_COMMA" to 44, "GLFW_KEY_MINUS" to 45,
        "GLFW_KEY_PERIOD" to 46, "GLFW_KEY_SLASH" to 47,
        "GLFW_KEY_0" to 48, "GLFW_KEY_1" to 49, "GLFW_KEY_2" to 50,
        "GLFW_KEY_3" to 51, "GLFW_KEY_4" to 52, "GLFW_KEY_5" to 53,
        "GLFW_KEY_6" to 54, "GLFW_KEY_7" to 55, "GLFW_KEY_8" to 56,
        "GLFW_KEY_9" to 57, "GLFW_KEY_SEMICOLON" to 59,
        "GLFW_KEY_EQUAL" to 61,
        "GLFW_KEY_A" to 65, "GLFW_KEY_B" to 66, "GLFW_KEY_C" to 67,
        "GLFW_KEY_D" to 68, "GLFW_KEY_E" to 69, "GLFW_KEY_F" to 70,
        "GLFW_KEY_G" to 71, "GLFW_KEY_H" to 72, "GLFW_KEY_I" to 73,
        "GLFW_KEY_J" to 74, "GLFW_KEY_K" to 75, "GLFW_KEY_L" to 76,
        "GLFW_KEY_M" to 77, "GLFW_KEY_N" to 78, "GLFW_KEY_O" to 79,
        "GLFW_KEY_P" to 80, "GLFW_KEY_Q" to 81, "GLFW_KEY_R" to 82,
        "GLFW_KEY_S" to 83, "GLFW_KEY_T" to 84, "GLFW_KEY_U" to 85,
        "GLFW_KEY_V" to 86, "GLFW_KEY_W" to 87, "GLFW_KEY_X" to 88,
        "GLFW_KEY_Y" to 89, "GLFW_KEY_Z" to 90,
        "GLFW_KEY_LEFT_BRACKET" to 91, "GLFW_KEY_BACKSLASH" to 92,
        "GLFW_KEY_RIGHT_BRACKET" to 93, "GLFW_KEY_GRAVE_ACCENT" to 96,
        "GLFW_KEY_ESCAPE" to 256, "GLFW_KEY_ENTER" to 257,
        "GLFW_KEY_TAB" to 258, "GLFW_KEY_BACKSPACE" to 259,
        "GLFW_KEY_INSERT" to 260, "GLFW_KEY_DELETE" to 261,
        "GLFW_KEY_RIGHT" to 262, "GLFW_KEY_LEFT" to 263,
        "GLFW_KEY_DOWN" to 264, "GLFW_KEY_UP" to 265,
        "GLFW_KEY_PAGE_UP" to 266, "GLFW_KEY_PAGE_DOWN" to 267,
        "GLFW_KEY_HOME" to 268, "GLFW_KEY_END" to 269,
        "GLFW_KEY_CAPS_LOCK" to 280, "GLFW_KEY_SCROLL_LOCK" to 281,
        "GLFW_KEY_NUM_LOCK" to 282, "GLFW_KEY_PRINT_SCREEN" to 283,
        "GLFW_KEY_PAUSE" to 284,
        "GLFW_KEY_F1" to 290, "GLFW_KEY_F2" to 291, "GLFW_KEY_F3" to 292,
        "GLFW_KEY_F4" to 293, "GLFW_KEY_F5" to 294, "GLFW_KEY_F6" to 295,
        "GLFW_KEY_F7" to 296, "GLFW_KEY_F8" to 297, "GLFW_KEY_F9" to 298,
        "GLFW_KEY_F10" to 299, "GLFW_KEY_F11" to 300, "GLFW_KEY_F12" to 301,
        "GLFW_KEY_F13" to 302, "GLFW_KEY_F14" to 303, "GLFW_KEY_F15" to 304,
        "GLFW_KEY_F16" to 305, "GLFW_KEY_F17" to 306, "GLFW_KEY_F18" to 307,
        "GLFW_KEY_F19" to 308, "GLFW_KEY_F20" to 309, "GLFW_KEY_F21" to 310,
        "GLFW_KEY_F22" to 311, "GLFW_KEY_F23" to 312, "GLFW_KEY_F24" to 313,
        "GLFW_KEY_F25" to 314,
        "GLFW_KEY_KP_0" to 320, "GLFW_KEY_KP_1" to 321, "GLFW_KEY_KP_2" to 322,
        "GLFW_KEY_KP_3" to 323, "GLFW_KEY_KP_4" to 324, "GLFW_KEY_KP_5" to 325,
        "GLFW_KEY_KP_6" to 326, "GLFW_KEY_KP_7" to 327, "GLFW_KEY_KP_8" to 328,
        "GLFW_KEY_KP_9" to 329, "GLFW_KEY_KP_DECIMAL" to 330,
        "GLFW_KEY_KP_DIVIDE" to 331, "GLFW_KEY_KP_MULTIPLY" to 332,
        "GLFW_KEY_KP_SUBTRACT" to 333, "GLFW_KEY_KP_ADD" to 334,
        "GLFW_KEY_KP_ENTER" to 335, "GLFW_KEY_KP_EQUAL" to 336,
        "GLFW_KEY_LEFT_SHIFT" to 340, "GLFW_KEY_LEFT_CONTROL" to 341,
        "GLFW_KEY_LEFT_ALT" to 342, "GLFW_KEY_LEFT_SUPER" to 343,
        "GLFW_KEY_RIGHT_SHIFT" to 344, "GLFW_KEY_RIGHT_CONTROL" to 345,
        "GLFW_KEY_RIGHT_ALT" to 346, "GLFW_KEY_RIGHT_SUPER" to 347,
        "GLFW_KEY_MENU" to 348,
        ControlEventKeycode.GLFW_MOUSE_BUTTON_LEFT to 0,
        ControlEventKeycode.GLFW_MOUSE_BUTTON_RIGHT to 1,
        ControlEventKeycode.GLFW_MOUSE_BUTTON_MIDDLE to 2
    )
}

private fun launcherEventToSpecialBtn(key: String): Int? = when (key) {
    LAUNCHER_EVENT_SWITCH_IME -> ZLControlData.SPECIALBTN_KEYBOARD
    LAUNCHER_EVENT_SWITCH_MENU -> ZLControlData.SPECIALBTN_MENU
    ControlEventKeycode.GLFW_MOUSE_BUTTON_LEFT -> ZLControlData.SPECIALBTN_MOUSEPRI
    ControlEventKeycode.GLFW_MOUSE_BUTTON_RIGHT -> ZLControlData.SPECIALBTN_MOUSESEC
    ControlEventKeycode.GLFW_MOUSE_BUTTON_MIDDLE -> ZLControlData.SPECIALBTN_MOUSEMID
    LAUNCHER_EVENT_SCROLL_UP, LAUNCHER_EVENT_SCROLL_UP_SINGLE -> ZLControlData.SPECIALBTN_SCROLLUP
    LAUNCHER_EVENT_SCROLL_DOWN, LAUNCHER_EVENT_SCROLL_DOWN_SINGLE -> ZLControlData.SPECIALBTN_SCROLLDOWN
    else -> null
}

private fun Color.toArgb(): Int {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

private fun normalDataToZLControlData(
    button: NormalData,
    screenWidthPx: Int,
    screenHeightPx: Int,
    density: Float,
    allStyles: List<ButtonStyle>
): ZLControlData {
    val styleConfig = button.buttonStyle
        ?.let { uuid -> allStyles.firstOrNull { it.uuid == uuid } }
        ?.lightStyle

    val data = ZLControlData()

    data.name = button.text.translate()

    val widthDp: Float
    val heightDp: Float
    when (button.buttonSize.type) {
        ButtonSize.Type.Dp -> {
            widthDp = button.buttonSize.widthDp.coerceAtLeast(5f)
            heightDp = button.buttonSize.heightDp.coerceAtLeast(5f)
        }
        else -> {
            val wRef = if (button.buttonSize.widthReference == ButtonSize.Reference.ScreenWidth)
                screenWidthPx else screenHeightPx
            val hRef = if (button.buttonSize.heightReference == ButtonSize.Reference.ScreenWidth)
                screenWidthPx else screenHeightPx
            widthDp = ((button.buttonSize.widthPercentage / 10000f) * wRef / density).coerceAtLeast(5f)
            heightDp = ((button.buttonSize.heightPercentage / 10000f) * hRef / density).coerceAtLeast(5f)
        }
    }

    data.width = widthDp
    data.height = heightDp

    val centerXPx = button.position.xPercentage() * screenWidthPx
    val centerYPx = button.position.yPercentage() * screenHeightPx
    val xPx = (centerXPx - widthDp * density / 2f).coerceAtLeast(0f)
    val yPx = (centerYPx - heightDp * density / 2f).coerceAtLeast(0f)

    data.dynamicX = xPx.toInt().toString()
    data.dynamicY = yPx.toInt().toString()

    data.isToggle = button.isToggleable
    data.passThruEnabled = button.isPenetrable
    data.isSwipeable = button.isSwipple

    data.displayInGame = button.visibilityType == VisibilityType.ALWAYS ||
        button.visibilityType == VisibilityType.IN_GAME
    data.displayInMenu = button.visibilityType == VisibilityType.ALWAYS ||
        button.visibilityType == VisibilityType.IN_MENU

    styleConfig?.let { sc ->
        data.opacity = sc.alpha
        data.bgColor = sc.backgroundColor.toArgb()
        data.strokeColor = sc.borderColor.toArgb()
        data.strokeWidth = sc.borderWidth.toFloat()
        val halfShorter = (minOf(widthDp, heightDp) / 2f).coerceAtLeast(1f)
        data.cornerRadius = ((sc.borderRadius.topStart / halfShorter) * 100f).coerceIn(0f, 100f)
    }

    val keycodes = IntArray(4) { 0 }
    var idx = 0
    for (event in button.clickEvents) {
        if (idx >= 4) break
        val kc: Int? = when (event.type) {
            ClickEvent.Type.Key -> EVENT_KEY_TO_GLFW_INT[event.key]
            ClickEvent.Type.LauncherEvent -> launcherEventToSpecialBtn(event.key)
            else -> null
        }
        if (kc != null) keycodes[idx++] = kc
    }
    data.keycodes = keycodes

    return data
}

fun ControlLayout.toZalithControls(
    screenWidthPx: Int = 1080,
    screenHeightPx: Int = 1920,
    density: Float = 2.75f
): ZLCustomControls {
    val result = ZLCustomControls()
    result.name = info.name.default
    result.author = info.author.default
    result.versionName = info.versionName
    for (layer in layers) {
        for (button in layer.normalButtons) {
            result.mControlDataList.add(
                normalDataToZLControlData(button, screenWidthPx, screenHeightPx, density, styles)
            )
        }
    }
    return result
}
