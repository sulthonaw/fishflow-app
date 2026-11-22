package com.fishflow.app.domain.usecase.tourism

import com.fishflow.app.data.model.Activity
import com.fishflow.app.data.model.BudgetEstimate

// CalculateBudgetEstimateUseCase.kt
// Developer: Fatih
//
// Hitung estimasi anggaran wisata berdasarkan aktivitas yang dipilih.
//
// Aturan:
// 1. Hanya aktivitas dengan biaya pasti yang masuk total.
// 2. Aktivitas dengan biaya null / isAvailable = false tidak dianggap Rp0,
//    tetapi dicatat di catatanBelumTersedia.
// 3. Biaya negatif dianggap tidak valid dan juga masuk catatan.
// 4. Aktivitas duplikat (id sama) hanya dihitung sekali.
class CalculateBudgetEstimateUseCase {

    operator fun invoke(activities: List<Activity>): BudgetEstimate {
        val distinctActivities = activities.distinctBy { it.id }
        val knownCostActivities = distinctActivities.filter { it.hasKnownCost }
        val unavailableActivities = distinctActivities.filterNot { it.hasKnownCost }

        return BudgetEstimate(
            selectedActivities = distinctActivities,
            totalBiaya = knownCostActivities.sumOf { it.biaya ?: 0.0 },
            catatanBelumTersedia = unavailableActivities.map { it.nama }
        )
    }
}
