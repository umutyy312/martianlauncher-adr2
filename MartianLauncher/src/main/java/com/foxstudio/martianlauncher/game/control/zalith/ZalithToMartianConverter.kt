package com.foxstudio.martianlauncher.game.control.zalith

import androidx.compose.ui.graphics.Color
import com.foxstudio.layer_controller.data.ButtonPosition
import com.foxstudio.layer_controller.data.ButtonShape
import com.foxstudio.layer_controller.data.ButtonSize
import com.foxstudio.layer_controller.data.ButtonStyle
import com.foxstudio.layer_controller.data.DefaultButtonStyleConfig
import com.foxstudio.layer_controller.data.NormalData
import com.foxstudio.layer_controller.data.VisibilityType
import com.foxstudio.layer_controller.event.ClickEvent
import com.foxstudio.layer_controller.layout.ControlLayer
import com.foxstudio.layer_controller.layout.ControlLayout
import com.foxstudio.layer_controller.layout.EmptyControlLayout
import com.foxstudio.layer_controller.layout.EmptyLayoutInfo
import com.foxstudio.layer_controller.utils.newRandomFileName
import com.foxstudio.layer_controller.data.lang.createTranslatable
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLControlData
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLCustomControls
import com.foxstudio.martianlauncher.game.keycodes.ControlEventKeycode
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_DOWN_SINGLE
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SCROLL_UP_SINGLE
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SWITCH_IME
import com.foxstudio.martianlauncher.ui.control.event.LAUNCHER_EVENT_SWITCH_MENU
import kotlin.math.min

private fun newUUID() = java.util.UUID.randomUUID().toString().replace("-", "").take(18)
private fun newShortUUID() = java.util.UUID.randomUUID().toString().replace("-", "").take(12)

private val GLFW_INT_TO_EVENT_KEY: Map<Int, String> by lazy {
    mapOf(
        32 to "GLFW_KEY_SPACE", 39 to "GLFW_KEY_APOSTROPHE",
        44 to "GLFW_KEY_COMMA", 45 to "GLFW_KEY_MINUS",
        46 to "GLFW_KEY_PERIOD", 47 to "GLFW_KEY_SLASH",
        48 to "GLFW_KEY_0", 49 to "GLFW_KEY_1", 50 to "GLFW_KEY_2",
        51 to "GLFW_KEY_3", 52 to "GLFW_KEY_4", 53 to "GLFW_KEY_5",
        54 to "GLFW_KEY_6", 55 to "GLFW_KEY_7", 56 to "GLFW_KEY_8",
        57 to "GLFW_KEY_9", 59 to "GLFW_KEY_SEMICOLON",
        61 to "GLFW_KEY_EQUAL",
        65 to "GLFW_KEY_A", 66 to "GLFW_KEY_B", 67 to "GLFW_KEY_C",
        68 to "GLFW_KEY_D", 69 to "GLFW_KEY_E", 70 to "GLFW_KEY_F",
        71 to "GLFW_KEY_G", 72 to "GLFW_KEY_H", 73 to "GLFW_KEY_I",
        74 to "GLFW_KEY_J", 75 to "GLFW_KEY_K", 76 to "GLFW_KEY_L",
        77 to "GLFW_KEY_M", 78 to "GLFW_KEY_N", 79 to "GLFW_KEY_O",
        80 to "GLFW_KEY_P", 81 to "GLFW_KEY_Q", 82 to "GLFW_KEY_R",
        83 to "GLFW_KEY_S", 84 to "GLFW_KEY_T", 85 to "GLFW_KEY_U",
        86 to "GLFW_KEY_V", 87 to "GLFW_KEY_W", 88 to "GLFW_KEY_X",
        89 to "GLFW_KEY_Y", 90 to "GLFW_KEY_Z",
        91 to "GLFW_KEY_LEFT_BRACKET", 92 to "GLFW_KEY_BACKSLASH",
        93 to "GLFW_KEY_RIGHT_BRACKET", 96 to "GLFW_KEY_GRAVE_ACCENT",
        256 to "GLFW_KEY_ESCAPE", 257 to "GLFW_KEY_ENTER",
        258 to "GLFW_KEY_TAB", 259 to "GLFW_KEY_BACKSPACE",
        260 to "GLFW_KEY_INSERT", 261 to "GLFW_KEY_DELETE",
        262 to "GLFW_KEY_RIGHT", 263 to "GLFW_KEY_LEFT",
        264 to "GLFW_KEY_DOWN", 265 to "GLFW_KEY_UP",
        266 to "GLFW_KEY_PAGE_UP", 267 to "GLFW_KEY_PAGE_DOWN",
        268 to "GLFW_KEY_HOME", 269 to "GLFW_KEY_END",
        280 to "GLFW_KEY_CAPS_LOCK", 281 to "GLFW_KEY_SCROLL_LOCK",
        282 to "GLFW_KEY_NUM_LOCK", 283 to "GLFW_KEY_PRINT_SCREEN",
        284 to "GLFW_KEY_PAUSE",
        290 to "GLFW_KEY_F1", 291 to "GLFW_KEY_F2", 292 to "GLFW_KEY_F3",
        293 to "GLFW_KEY_F4", 294 to "GLFW_KEY_F5", 295 to "GLFW_KEY_F6",
        296 to "GLFW_KEY_F7", 297 to "GLFW_KEY_F8", 298 to "GLFW_KEY_F9",
        299 to "GLFW_KEY_F10", 300 to "GLFW_KEY_F11", 301 to "GLFW_KEY_F12",
        302 to "GLFW_KEY_F13", 303 to "GLFW_KEY_F14", 304 to "GLFW_KEY_F15",
        305 to "GLFW_KEY_F16", 306 to "GLFW_KEY_F17", 307 to "GLFW_KEY_F18",
        308 to "GLFW_KEY_F19", 309 to "GLFW_KEY_F20", 310 to "GLFW_KEY_F21",
        311 to "GLFW_KEY_F22", 312 to "GLFW_KEY_F23", 313 to "GLFW_KEY_F24",
        314 to "GLFW_KEY_F25",
        320 to "GLFW_KEY_KP_0", 321 to "GLFW_KEY_KP_1", 322 to "GLFW_KEY_KP_2",
        323 to "GLFW_KEY_KP_3", 324 to "GLFW_KEY_KP_4", 325 to "GLFW_KEY_KP_5",
        326 to "GLFW_KEY_KP_6", 327 to "GLFW_KEY_KP_7", 328 to "GLFW_KEY_KP_8",
        329 to "GLFW_KEY_KP_9", 330 to "GLFW_KEY_KP_DECIMAL",
        331 to "GLFW_KEY_KP_DIVIDE", 332 to "GLFW_KEY_KP_MULTIPLY",
        333 to "GLFW_KEY_KP_SUBTRACT", 334 to "GLFW_KEY_KP_ADD",
        335 to "GLFW_KEY_KP_ENTER", 336 to "GLFW_KEY_KP_EQUAL",
        340 to "GLFW_KEY_LEFT_SHIFT", 341 to "GLFW_KEY_LEFT_CONTROL",
        342 to "GLFW_KEY_LEFT_ALT", 343 to "GLFW_KEY_LEFT_SUPER",
        344 to "GLFW_KEY_RIGHT_SHIFT", 345 to "GLFW_KEY_RIGHT_CONTROL",
        346 to "GLFW_KEY_RIGHT_ALT", 347 to "GLFW_KEY_RIGHT_SUPER",
        348 to "GLFW_KEY_MENU",
        0 to "GLFW_MOUSE_BUTTON_LEFT",
        1 to "GLFW_MOUSE_BUTTON_RIGHT",
        2 to "GLFW_MOUSE_BUTTON_MIDDLE"
    )
}

private fun specialBtnToClickEvents(keycode: Int): List<ClickEvent> {
    val event = when (keycode) {
        ZLControlData.SPECIALBTN_KEYBOARD ->
            ClickEvent(ClickEvent.Type.LauncherEvent, LAUNCHER_EVENT_SWITCH_IME)
        ZLControlData.SPECIALBTN_MENU ->
            ClickEvent(ClickEvent.Type.LauncherEvent, LAUNCHER_EVENT_SWITCH_MENU)
        ZLControlData.SPECIALBTN_MOUSEPRI ->
            ClickEvent(ClickEvent.Type.LauncherEvent, ControlEventKeycode.GLFW_MOUSE_BUTTON_LEFT)
        ZLControlData.SPECIALBTN_MOUSESEC ->
            ClickEvent(ClickEvent.Type.LauncherEvent, ControlEventKeycode.GLFW_MOUSE_BUTTON_RIGHT)
        ZLControlData.SPECIALBTN_MOUSEMID ->
            ClickEvent(ClickEvent.Type.LauncherEvent, ControlEventKeycode.GLFW_MOUSE_BUTTON_MIDDLE)
        ZLControlData.SPECIALBTN_SCROLLUP ->
            ClickEvent(ClickEvent.Type.LauncherEvent, LAUNCHER_EVENT_SCROLL_UP_SINGLE)
        ZLControlData.SPECIALBTN_SCROLLDOWN ->
            ClickEvent(ClickEvent.Type.LauncherEvent, LAUNCHER_EVENT_SCROLL_DOWN_SINGLE)
        else -> null
    }
    return listOfNotNull(event)
}

private fun zlColorToCompose(argb: Int): Color {
    val a = ((argb shr 24) and 0xFF) / 255f
    val r = ((argb shr 16) and 0xFF) / 255f
    val g = ((argb shr 8) and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    return Color(r, g, b, a)
}

private fun zlDataToNormalData(
    data: ZLControlData,
    screenWidthPx: Int,
    screenHeightPx: Int,
    density: Float,
    styleUuid: String
): NormalData {
    val widthPx = data.width * density
    val heightPx = data.height * density

    val xPx = ZalithPositionEvaluator.evaluate(
        expression = data.dynamicX,
        buttonWidthPx = widthPx,
        buttonHeightPx = heightPx,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        density = density
    )
    val yPx = ZalithPositionEvaluator.evaluate(
        expression = data.dynamicY,
        buttonWidthPx = widthPx,
        buttonHeightPx = heightPx,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        density = density
    )

    val xPct = ((xPx / screenWidthPx.toFloat()) * 10000).toInt().coerceIn(0, 10000)
    val yPct = ((yPx / screenHeightPx.toFloat()) * 10000).toInt().coerceIn(0, 10000)

    val clickEvents = buildList {
        for (kc in data.keycodes) {
            if (kc == 0) continue
            if (kc > 0) {
                val eventKey = GLFW_INT_TO_EVENT_KEY[kc] ?: continue
                val isMouse = eventKey.startsWith("GLFW_MOUSE_")
                add(ClickEvent(
                    type = if (isMouse) ClickEvent.Type.LauncherEvent else ClickEvent.Type.Key,
                    key = eventKey
                ))
            } else {
                addAll(specialBtnToClickEvents(kc))
            }
        }
    }

    val visibility = when {
        data.displayInGame && data.displayInMenu -> VisibilityType.ALWAYS
        data.displayInGame -> VisibilityType.IN_GAME
        else -> VisibilityType.IN_MENU
    }

    return NormalData(
        text = createTranslatable(default = data.name),
        uuid = newUUID(),
        position = ButtonPosition(xPct, yPct),
        buttonSize = ButtonSize(
            type = ButtonSize.Type.Dp,
            widthDp = data.width.coerceAtLeast(5f),
            heightDp = data.height.coerceAtLeast(5f),
            widthPercentage = 1000,
            heightPercentage = 1000,
            widthReference = ButtonSize.Reference.ScreenHeight,
            heightReference = ButtonSize.Reference.ScreenHeight
        ),
        buttonStyle = styleUuid,
        visibilityType = visibility,
        _clickEvents = clickEvents,
        isSwipple = data.isSwipeable,
        isPenetrable = data.passThruEnabled,
        isToggleable = data.isToggle
    )
}

private fun zlDataToButtonStyle(data: ZLControlData): ButtonStyle {
    val bg = zlColorToCompose(data.bgColor)
    val stroke = zlColorToCompose(data.strokeColor)
    val cornerDp = (min(data.width, data.height) / 2f) * (data.cornerRadius / 100f)
    val shape = ButtonShape(cornerDp)

    val styleConfig = DefaultButtonStyleConfig.copy(
        alpha = data.opacity,
        pressedAlpha = (data.opacity * 0.7f).coerceIn(0f, 1f),
        backgroundColor = bg,
        pressedBackgroundColor = bg.copy(alpha = (bg.alpha * 0.7f).coerceIn(0f, 1f)),
        borderWidth = data.strokeWidth.toInt().coerceIn(0, 50),
        pressedBorderWidth = data.strokeWidth.toInt().coerceIn(0, 50),
        borderColor = stroke,
        pressedBorderColor = stroke,
        borderRadius = shape,
        pressedBorderRadius = shape
    )

    return ButtonStyle(
        name = if (data.name.isNotBlank()) data.name else "btn_${newRandomFileName(4)}",
        uuid = newShortUUID(),
        animateSwap = false,
        commonStyle = true,
        lightStyle = styleConfig,
        darkStyle = styleConfig
    )
}

fun ZLCustomControls.toMartianLayout(
    name: String,
    screenWidthPx: Int = 1080,
    screenHeightPx: Int = 1920,
    density: Float = 2.75f
): ControlLayout {
    val styles = mutableListOf<ButtonStyle>()
    val buttons = mutableListOf<NormalData>()

    for (data in mControlDataList) {
        val style = zlDataToButtonStyle(data)
        styles.add(style)
        buttons.add(
            zlDataToNormalData(data, screenWidthPx, screenHeightPx, density, style.uuid)
        )
    }

    for (drawerData in mDrawerDataList) {
        val style = zlDataToButtonStyle(drawerData.properties)
        styles.add(style)
        buttons.add(
            zlDataToNormalData(drawerData.properties, screenWidthPx, screenHeightPx, density, style.uuid)
        )
        for (btn in drawerData.buttonProperties) {
            val s = zlDataToButtonStyle(btn)
            styles.add(s)
            buttons.add(zlDataToNormalData(btn, screenWidthPx, screenHeightPx, density, s.uuid))
        }
    }

    val layer = ControlLayer(
        name = "Zalith",
        uuid = newShortUUID(),
        hide = false,
        hideWhenMouse = false,
        hideWhenGamepad = false,
        hideWhenJoystick = false,
        visibilityType = VisibilityType.ALWAYS,
        normalButtons = buttons
    )

    return ControlLayout(
        info = EmptyLayoutInfo.copy(
            name = createTranslatable(default = name)
        ),
        layers = listOf(layer),
        styles = styles,
        editorVersion = EmptyControlLayout.editorVersion
    )
}
