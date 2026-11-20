package com.fishflow.app.feature.tourist.budget.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishflow.app.data.model.Activity
import com.fishflow.app.feature.tourist.destination.components.ActivityChecklistItem

// ActivitySelector.kt
// Developer: Fatih
// Daftar aktivitas dengan checkbox. Stateless.
// Terima: List<Activity>, selectedIds (Set), onToggle lambda.
@Composable
fun ActivitySelector(
    activities: List<Activity>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (activities.isEmpty()) {
            Text(
                text = "Belum ada aktivitas yang terdaftar untuk destinasi ini.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            activities.forEachIndexed { index, activity ->
                ActivityChecklistItem(
                    activity = activity,
                    checked = activity.id in selectedIds,
                    onCheckedChange = { onToggle(activity.id) }
                )
                if (index != activities.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}
