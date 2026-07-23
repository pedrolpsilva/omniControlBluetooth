package com.omnicontrolbluetooth.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DotsSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    numDots: Int = 15
) {
    var width by remember { mutableIntStateOf(0) }
    val selectedIndex = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start) * (numDots - 1))
        .roundToInt()
        .coerceIn(0, numDots - 1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .onGloballyPositioned { width = it.size.width }
            .pointerInput(numDots, valueRange) {
                detectTapGestures { offset ->
                    val pct = (offset.x / width).coerceIn(0f, 1f)
                    val idx = (pct * (numDots - 1)).roundToInt()
                    val newValue = valueRange.start + idx * ((valueRange.endInclusive - valueRange.start) / (numDots - 1))
                    onValueChange(newValue)
                }
            }
            .pointerInput(numDots, valueRange) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    val pct = (change.position.x / width).coerceIn(0f, 1f)
                    val idx = (pct * (numDots - 1)).roundToInt()
                    val newValue = valueRange.start + idx * ((valueRange.endInclusive - valueRange.start) / (numDots - 1))
                    onValueChange(newValue)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until numDots) {
            val isSelected = i == selectedIndex
            Box(
                modifier = Modifier
                    .size(if (isSelected) 8.dp else 5.dp)
                    .background(
                        color = if (isSelected) Color.White else Color(0xFF333333),
                        shape = CircleShape
                    )
            )
        }
    }
}

