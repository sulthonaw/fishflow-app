package com.fishflow.app.feature.tourist.budget

import com.fishflow.app.data.model.Activity
import java.text.NumberFormat
import java.util.Locale

// BudgetPlannerUiState.kt
// Developer: Fatih
//
// State untuk halaman estimasi anggaran.
// Field: destinationName (String), allActivities (List<Activity>),
//        selectedActivityIds (Set<String>), totalBiaya (Double),
//        catatanBelumTersedia (List<String>), isLoading
data class BudgetPlannerUiState(
    val destinationId: String = "",
    val destinationName: String = "",
    val allActivities: List<Activity> = emptyList(),
    val selectedActivityIds: Set<String> = emptySet(),
    val totalBiaya: Double = 0.0,
    val catatanBelumTersedia: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val selectedActivities: List<Activity>
        get() = allActivities.filter { it.id in selectedActivityIds }

    val hasUnavailableCost: Boolean
        get() = catatanBelumTersedia.isNotEmpty()

    val selectedCount: Int
        get() = selectedActivityIds.size
}

internal fun formatRupiah(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    formatter.maximumFractionDigits = 0
    return "Rp${formatter.format(value)}"
}
