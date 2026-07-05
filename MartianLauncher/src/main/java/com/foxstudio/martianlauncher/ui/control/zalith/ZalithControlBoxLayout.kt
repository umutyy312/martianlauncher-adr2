package com.foxstudio.martianlauncher.ui.control.zalith

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.foxstudio.martianlauncher.game.control.zalith.ZalithPositionEvaluator
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLControlData
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLControlDrawerData
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLControlJoystickData
import com.foxstudio.martianlauncher.game.control.zalith.model.ZLCustomControls
import org.lwjgl.glfw.CallbackBridge
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun ZalithControlBoxLayout(
    layout: ZLCustomControls,
    isGrabbing: Boolean,
    screenWidthPx: Int,
    screenHeightPx: Int,
    globalOpacity: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    Box(modifier = modifier.fillMaxSize()) {
        layout.mControlDataList.forEach { data ->
            val visible = if (isGrabbing) data.displayInGame else data.displayInMenu
            if (visible) {
                ZalithControlButton(
                    data = data,
                    screenWidthPx = screenWidthPx,
                    screenHeightPx = screenHeightPx,
                    density = density,
                    globalOpacity = globalOpacity
                )
            }
        }

        layout.mDrawerDataList.forEach { drawerData ->
            val visible = if (isGrabbing) drawerData.properties.displayInGame else drawerData.properties.displayInMenu
            if (visible) {
                ZalithDrawerButton(
                    drawerData = drawerData,
                    screenWidthPx = screenWidthPx,
                    screenHeightPx = screenHeightPx,
                    density = density,
                    globalOpacity = globalOpacity
                )
            }
        }

        layout.mJoystickDataList.forEach { joystickData ->
            val visible = if (isGrabbing) joystickData.displayInGame else joystickData.displayInMenu
            if (visible) {
                ZalithJoystickButton(
                    data = joystickData,
                    screenWidthPx = screenWidthPx,
                    screenHeightPx = screenHeightPx,
                    density = density,
                    globalOpacity = globalOpacity
                )
            }
        }
    }
}

@Composable
private fun ZalithControlButton(
    data: ZLControlData,
    screenWidthPx: Int,
    screenHeightPx: Int,
    density: Float,
    globalOpacity: Float
) {
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

    val cornerRadiusPx = (min(widthPx, heightPx) / 2f) * (data.cornerRadius / 100f)
    val shape = RoundedCornerShape(with(LocalDensity.current) { cornerRadiusPx.toDp() })

    var pressed by remember { mutableStateOf(false) }
    var toggledOn by remember { mutableStateOf(false) }

    val widthDp = with(LocalDensity.current) { widthPx.toDp() }
    val heightDp = with(LocalDensity.current) { heightPx.toDp() }

    if (data.isSwipeable) {
        ZalithSwipeableButton(
            data = data,
            xPx = xPx,
            yPx = yPx,
            widthPx = widthPx,
            heightPx = heightPx,
            density = density,
            globalOpacity = globalOpacity,
            shape = shape
        )
        return
    }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(xPx.roundToInt(), yPx.roundToInt())
                }
            }
            .size(width = widthDp, height = heightDp)
            .graphicsLayer { alpha = (data.opacity * globalOpacity).coerceIn(0f, 1f) }
            .clip(shape)
            .background(if (pressed || toggledOn) Color(data.bgColor).copy(alpha = 0.75f) else Color(data.bgColor))
            .then(
                if (data.strokeWidth > 0f) {
                    Modifier.border(
                        width = with(LocalDensity.current) { (data.strokeWidth * density).toDp() },
                        color = Color(data.strokeColor),
                        shape = shape
                    )
                } else Modifier
            )
            .pointerInput(data) {
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitPointerEvent(PointerEventPass.Main)
                        val downChange = down.changes.firstOrNull { it.pressed }
                        if (downChange != null && !pressed) {
                            if (data.isToggle) {
                                toggledOn = !toggledOn
                                dispatchKeys(data, toggledOn)
                            } else {
                                pressed = true
                                dispatchKeys(data, true)
                            }
                            while (true) {
                                val ev = awaitPointerEvent(PointerEventPass.Main)
                                if (ev.changes.all { !it.pressed }) {
                                    if (!data.isToggle) {
                                        pressed = false
                                        dispatchKeys(data, false)
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = data.name,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ZalithSwipeableButton(
    data: ZLControlData,
    xPx: Float,
    yPx: Float,
    widthPx: Float,
    heightPx: Float,
    density: Float,
    globalOpacity: Float,
    shape: RoundedCornerShape
) {
    val widthDp = with(LocalDensity.current) { widthPx.toDp() }
    val heightDp = with(LocalDensity.current) { heightPx.toDp() }

    var activeKeyIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(xPx.roundToInt(), yPx.roundToInt())
                }
            }
            .size(width = widthDp, height = heightDp)
            .graphicsLayer { alpha = (data.opacity * globalOpacity).coerceIn(0f, 1f) }
            .clip(shape)
            .background(Color(data.bgColor))
            .then(
                if (data.strokeWidth > 0f) {
                    Modifier.border(
                        width = with(LocalDensity.current) { (data.strokeWidth * density).toDp() },
                        color = Color(data.strokeColor),
                        shape = shape
                    )
                } else Modifier
            )
            .pointerInput(data) {
                detectDragGestures(
                    onDragStart = {
                        if (activeKeyIndex < 0) {
                            activeKeyIndex = 0
                            if (data.keycodes.isNotEmpty() && data.keycodes[0] != 0) {
                                ZalithControlEventDispatcher.sendZalithKeycode(data.keycodes[0], true)
                            }
                        }
                    },
                    onDragEnd = {
                        if (activeKeyIndex >= 0) {
                            val kc = data.keycodes.getOrElse(activeKeyIndex) { 0 }
                            if (kc != 0) ZalithControlEventDispatcher.sendZalithKeycode(kc, false)
                            activeKeyIndex = -1
                        }
                    },
                    onDragCancel = {
                        if (activeKeyIndex >= 0) {
                            val kc = data.keycodes.getOrElse(activeKeyIndex) { 0 }
                            if (kc != 0) ZalithControlEventDispatcher.sendZalithKeycode(kc, false)
                            activeKeyIndex = -1
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val dx = dragAmount.x
                        val dy = dragAmount.y
                        if (abs(dx) < 2f && abs(dy) < 2f) return@detectDragGestures

                        val newIndex = when {
                            abs(dx) > abs(dy) -> if (dx > 0) 1 else 3
                            else -> if (dy < 0) 2 else 0
                        }

                        if (newIndex != activeKeyIndex) {
                            val oldKc = data.keycodes.getOrElse(activeKeyIndex) { 0 }
                            if (activeKeyIndex >= 0 && oldKc != 0) {
                                ZalithControlEventDispatcher.sendZalithKeycode(oldKc, false)
                            }
                            activeKeyIndex = newIndex
                            val newKc = data.keycodes.getOrElse(newIndex) { 0 }
                            if (newKc != 0) {
                                ZalithControlEventDispatcher.sendZalithKeycode(newKc, true)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = data.name,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ZalithDrawerButton(
    drawerData: ZLControlDrawerData,
    screenWidthPx: Int,
    screenHeightPx: Int,
    density: Float,
    globalOpacity: Float
) {
    val props = drawerData.properties

    val widthPx = props.width * density
    val heightPx = props.height * density

    val xPx = ZalithPositionEvaluator.evaluate(
        expression = props.dynamicX,
        buttonWidthPx = widthPx,
        buttonHeightPx = heightPx,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        density = density
    )
    val yPx = ZalithPositionEvaluator.evaluate(
        expression = props.dynamicY,
        buttonWidthPx = widthPx,
        buttonHeightPx = heightPx,
        screenWidthPx = screenWidthPx,
        screenHeightPx = screenHeightPx,
        density = density
    )

    val cornerRadiusPx = (min(widthPx, heightPx) / 2f) * (props.cornerRadius / 100f)
    val shape = RoundedCornerShape(with(LocalDensity.current) { cornerRadiusPx.toDp() })
    val widthDp = with(LocalDensity.current) { widthPx.toDp() }
    val heightDp = with(LocalDensity.current) { heightPx.toDp() }

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(xPx.roundToInt(), yPx.roundToInt())
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(width = widthDp, height = heightDp)
                .graphicsLayer { alpha = (props.opacity * globalOpacity).coerceIn(0f, 1f) }
                .clip(shape)
                .background(Color(props.bgColor))
                .then(
                    if (props.strokeWidth > 0f) {
                        Modifier.border(
                            width = with(LocalDensity.current) { (props.strokeWidth * density).toDp() },
                            color = Color(props.strokeColor),
                            shape = shape
                        )
                    } else Modifier
                )
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitPointerEvent(PointerEventPass.Main)
                            if (down.changes.any { it.pressed }) {
                                expanded = !expanded
                                while (true) {
                                    val ev = awaitPointerEvent(PointerEventPass.Main)
                                    if (ev.changes.all { !it.pressed }) break
                                }
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = props.name, color = Color.White, textAlign = TextAlign.Center)
        }

        if (expanded) {
            drawerData.buttonProperties.forEachIndexed { index, buttonData ->
                val btnWidthPx = buttonData.width * density
                val btnHeightPx = buttonData.height * density
                val btnWidthDp = with(LocalDensity.current) { btnWidthPx.toDp() }
                val btnHeightDp = with(LocalDensity.current) { btnHeightPx.toDp() }
                val btnShape = RoundedCornerShape(
                    with(LocalDensity.current) {
                        ((min(btnWidthPx, btnHeightPx) / 2f) * (buttonData.cornerRadius / 100f)).toDp()
                    }
                )

                val offsetDp = with(LocalDensity.current) { (widthPx + 4f * density).toDp() }
                val stepDp = with(LocalDensity.current) { ((btnHeightPx + 4f * density) * index).toDp() }

                var pressed by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .offset(x = offsetDp, y = stepDp)
                        .size(width = btnWidthDp, height = btnHeightDp)
                        .graphicsLayer { alpha = (buttonData.opacity * globalOpacity).coerceIn(0f, 1f) }
                        .clip(btnShape)
                        .background(if (pressed) Color(buttonData.bgColor).copy(alpha = 0.75f) else Color(buttonData.bgColor))
                        .then(
                            if (buttonData.strokeWidth > 0f) {
                                Modifier.border(
                                    width = with(LocalDensity.current) { (buttonData.strokeWidth * density).toDp() },
                                    color = Color(buttonData.strokeColor),
                                    shape = btnShape
                                )
                            } else Modifier
                        )
                        .pointerInput(buttonData) {
                            awaitPointerEventScope {
                                while (true) {
                                    val down = awaitPointerEvent(PointerEventPass.Main)
                                    if (down.changes.any { it.pressed } && !pressed) {
                                        pressed = true
                                        dispatchKeys(buttonData, true)
                                        while (true) {
                                            val ev = awaitPointerEvent(PointerEventPass.Main)
                                            if (ev.changes.all { !it.pressed }) {
                                                pressed = false
                                                dispatchKeys(buttonData, false)
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = buttonData.name, color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun ZalithJoystickButton(
    data: ZLControlJoystickData,
    screenWidthPx: Int,
    screenHeightPx: Int,
    density: Float,
    globalOpacity: Float
) {
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

    val cornerRadiusPx = (min(widthPx, heightPx) / 2f) * (data.cornerRadius / 100f)
    val shape = RoundedCornerShape(with(LocalDensity.current) { cornerRadiusPx.toDp() })
    val widthDp = with(LocalDensity.current) { widthPx.toDp() }
    val heightDp = with(LocalDensity.current) { heightPx.toDp() }

    val thumbRadiusPx = min(widthPx, heightPx) * 0.25f
    val thumbRadiusDp = with(LocalDensity.current) { thumbRadiusPx.toDp() }
    val maxThumbTravelPx = min(widthPx, heightPx) / 2f - thumbRadiusPx

    var thumbDx by remember { mutableFloatStateOf(0f) }
    var thumbDy by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(xPx.roundToInt(), yPx.roundToInt())
                }
            }
            .size(width = widthDp, height = heightDp)
            .graphicsLayer { alpha = (data.opacity * globalOpacity).coerceIn(0f, 1f) }
            .clip(shape)
            .background(Color(data.bgColor))
            .then(
                if (data.strokeWidth > 0f) {
                    Modifier.border(
                        width = with(LocalDensity.current) { (data.strokeWidth * density).toDp() },
                        color = Color(data.strokeColor),
                        shape = shape
                    )
                } else Modifier
            )
            .pointerInput(data) {
                detectDragGestures(
                    onDragStart = {
                        thumbDx = 0f
                        thumbDy = 0f
                        dispatchKeys(data, true)
                    },
                    onDragEnd = {
                        thumbDx = 0f
                        thumbDy = 0f
                        CallbackBridge.sendCursorDelta(0f, 0f)
                        dispatchKeys(data, false)
                    },
                    onDragCancel = {
                        thumbDx = 0f
                        thumbDy = 0f
                        dispatchKeys(data, false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newDx = (thumbDx + dragAmount.x).coerceIn(-maxThumbTravelPx, maxThumbTravelPx)
                        val newDy = (thumbDy + dragAmount.y).coerceIn(-maxThumbTravelPx, maxThumbTravelPx)
                        val dist = sqrt(newDx * newDx + newDy * newDy)
                        if (dist > maxThumbTravelPx) {
                            val scale = maxThumbTravelPx / dist
                            thumbDx = newDx * scale
                            thumbDy = newDy * scale
                        } else {
                            thumbDx = newDx
                            thumbDy = newDy
                        }
                        val normX = thumbDx / maxThumbTravelPx.coerceAtLeast(1f)
                        val normY = thumbDy / maxThumbTravelPx.coerceAtLeast(1f)
                        CallbackBridge.sendCursorDelta(normX * 5f, normY * 5f)
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(thumbRadiusDp * 2)
                .offset(
                    x = with(LocalDensity.current) { (thumbDx + widthPx / 2f - thumbRadiusPx).toDp() },
                    y = with(LocalDensity.current) { (thumbDy + heightPx / 2f - thumbRadiusPx).toDp() }
                )
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.6f))
        )
        Text(
            text = data.name,
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

private fun dispatchKeys(data: ZLControlData, isPressed: Boolean) {
    for (keycode in data.keycodes) {
        if (keycode == 0) continue
        ZalithControlEventDispatcher.sendZalithKeycode(keycode, isPressed)
    }
}
