package com.omnicontrolbluetooth.ui.modifiers

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import com.omnicontrolbluetooth.BluetoothHidDeviceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.trackpadGestures(
    sensitivity: Float,
    btManager: BluetoothHidDeviceManager,
    coroutineScope: CoroutineScope,
    isFullscreen: Boolean = false
): Modifier = this.pointerInput(sensitivity, isFullscreen) {
    var lastTapTime = 0L
    var lastTapPos = androidx.compose.ui.geometry.Offset.Zero

    awaitEachGesture {
        val firstDown = awaitFirstDown(requireUnconsumed = false)
        val downTime = System.currentTimeMillis()
        val downPos = firstDown.position

        if (isFullscreen && downPos.y > size.height * 0.75f) {
            return@awaitEachGesture
        }

        firstDown.consume()

        val isDoubleTapDrag = (downTime - lastTapTime < 300L) && 
                              ((downPos - lastTapPos).getDistance() < 100f)

        var dragWithClick = isDoubleTapDrag

        if (dragWithClick) {
            coroutineScope.launch(Dispatchers.IO) {
                btManager.sendMouseReport(buttons = 1)
            }
        }

        var maxPointers = 1
        var totalMovement = 0f
        val lastPositions = mutableMapOf(firstDown.id to firstDown.position)

        var accX = 0f
        var accY = 0f
        var accScroll = 0f
        
        var twoFingersStartPos: List<androidx.compose.ui.geometry.Offset>? = null
        var twoFingersLastY = 0f
        var twoFingersGestureType = 0 
        var swipeTriggered = false

        var event: PointerEvent
        var lastReleasedPosition = downPos

        do {
            event = awaitPointerEvent()
            val active = event.changes.filter { it.pressed }

            if (active.isEmpty()) break

            maxPointers = maxOf(maxPointers, active.size)

            val newPointers = active.filter { it.id !in lastPositions }
            if (newPointers.isNotEmpty()) {
                active.forEach { if (it.id !in lastPositions) lastPositions[it.id] = it.position }
                event.changes.forEach { it.consume() }
                continue
            }

            val isTrueTwoFingers = active.size >= 2 && 
                (active[0].position - active[1].position).getDistance() > 80f

            when {
                active.size == 1 || (active.size >= 2 && !isTrueTwoFingers) -> {
                    val c    = active[0]
                    val last = lastPositions[c.id] ?: c.position
                    val dx   = c.position.x - last.x
                    val dy   = c.position.y - last.y

                    if (dx != 0f || dy != 0f) {
                        totalMovement += abs(dx) + abs(dy)

                        accX += dx * sensitivity
                        accY += dy * sensitivity
                        val rdx = accX.toInt().coerceIn(-127, 127)
                        val rdy = accY.toInt().coerceIn(-127, 127)
                        accX -= rdx
                        accY -= rdy

                        if (rdx != 0 || rdy != 0) {
                            coroutineScope.launch(Dispatchers.IO) {
                                btManager.sendMouseReport(
                                    dx = rdx, 
                                    dy = rdy, 
                                    buttons = if (dragWithClick) 1 else 0
                                )
                            }
                        }
                    }
                    lastPositions[c.id] = c.position
                    lastReleasedPosition = c.position
                    c.consume()
                }

                isTrueTwoFingers -> {
                    if (dragWithClick) {
                        dragWithClick = false
                        coroutineScope.launch(Dispatchers.IO) {
                            btManager.sendMouseReport(buttons = 0)
                        }
                    }

                    if (twoFingersStartPos == null) {
                        twoFingersStartPos = active.take(2).map { it.position }
                        twoFingersLastY = active.take(2).map { it.position.y }.average().toFloat()
                    }

                    val currentAvgX = active.take(2).map { it.position.x }.average().toFloat()
                    val currentAvgY = active.take(2).map { it.position.y }.average().toFloat()

                    val startAvgX = twoFingersStartPos?.map { it.x }?.average()?.toFloat() ?: currentAvgX
                    val startAvgY = twoFingersStartPos?.map { it.y }?.average()?.toFloat() ?: currentAvgY

                    val totalDX = currentAvgX - startAvgX
                    val totalDY = currentAvgY - startAvgY

                    if (twoFingersGestureType == 0) {
                        if (abs(totalDX) > 30f && abs(totalDX) > abs(totalDY)) {
                            twoFingersGestureType = 2 
                        } else if (abs(totalDY) > 30f && abs(totalDY) > abs(totalDX)) {
                            twoFingersGestureType = 1 
                        }
                    }

                    if (twoFingersGestureType == 2) {
                        if (!swipeTriggered) {
                            if (totalDX < -150f) {
                                swipeTriggered = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    btManager.sendMouseReport(buttons = 16) 
                                    delay(60)
                                    btManager.sendMouseReport(buttons = 0)
                                }
                            } else if (totalDX > 150f) {
                                swipeTriggered = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    btManager.sendMouseReport(buttons = 8) 
                                    delay(60)
                                    btManager.sendMouseReport(buttons = 0)
                                }
                            }
                        }
                    } else if (twoFingersGestureType == 1) {
                        val dy = currentAvgY - twoFingersLastY
                        if (abs(dy) > 0f) {
                            totalMovement += abs(dy)
                            accScroll += -dy / 20f
                            val scrollVal = accScroll.toInt().coerceIn(-5, 5)
                            accScroll -= scrollVal
                            if (scrollVal != 0) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    btManager.sendMouseReport(scroll = scrollVal)
                                }
                            }
                        }
                    }

                    twoFingersLastY = currentAvgY

                    active.forEach { c ->
                        lastPositions[c.id] = c.position
                        c.consume()
                    }
                }
            }
        } while (event.changes.any { it.pressed })

        val releaseTime = System.currentTimeMillis()

        if (dragWithClick) {
            coroutineScope.launch(Dispatchers.IO) {
                btManager.sendMouseReport(buttons = 0)
            }
        } else {
            if (totalMovement < 20f && !swipeTriggered && twoFingersGestureType == 0) {
                val finalPointers = if (twoFingersStartPos != null) 2 else maxPointers
                when (finalPointers) {
                    1 -> {
                        coroutineScope.launch(Dispatchers.IO) {
                            btManager.sendMouseReport(buttons = 1)
                            delay(50)
                            btManager.sendMouseReport(buttons = 0)
                        }
                        lastTapTime = releaseTime
                        lastTapPos = lastReleasedPosition
                    }
                    2 -> {
                        coroutineScope.launch(Dispatchers.IO) {
                            btManager.sendMouseReport(buttons = 2)
                            delay(50)
                            btManager.sendMouseReport(buttons = 0)
                        }
                    }
                }
            }
        }
    }
}

