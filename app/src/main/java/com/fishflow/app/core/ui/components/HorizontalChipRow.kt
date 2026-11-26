// HorizontalChipRow.kt
// Developer: Octavia
//
// Baris chip yang bisa digeser ke samping sampai tepi layar. Stateless.
// horizontalScroll bawaan Compose hanya menerima geseran sentuhan, jadi geseran mouse
// (misalnya di emulator) ditangani draggable.
package com.fishflow.app.core.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalChipRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    val mouseDragState = rememberDraggableState { delta -> scrollState.dispatchRawDelta(-delta) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .draggable(state = mouseDragState, orientation = Orientation.Horizontal)
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}
