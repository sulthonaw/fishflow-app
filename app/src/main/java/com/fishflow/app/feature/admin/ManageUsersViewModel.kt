package com.fishflow.app.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ManageUsersViewModel.kt
// Developer: Fatih
//
// Koneksi: Bagian dari modul admin yang Fatih pegang.
//
// ViewModel kelola akun user. Ambil semua user, filter by role.
// Karena UserRepository/AuthRemoteDataSource masih dikerjakan Fakhri,
// data user untuk sementara disimpan in-memory di AdminUserDirectory.
enum class AdminUserRole(val label: String) {
    WISATAWAN("Wisatawan"),
    PENJUAL("Penjual"),
    ADMIN("Admin")
}

data class ManagedUser(
    val id: String,
    val nama: String,
    val email: String,
    val role: AdminUserRole,
    val isActive: Boolean = true
)

object AdminUserDirectory {

    private val users = mutableListOf(
        ManagedUser("user-1", "Budi Santoso", "budi@example.com", AdminUserRole.WISATAWAN),
        ManagedUser("user-2", "Siti Aminah", "siti@example.com", AdminUserRole.WISATAWAN),
        ManagedUser("user-3", "Pak Slamet", "slamet@example.com", AdminUserRole.PENJUAL),
        ManagedUser("user-4", "Nelayan Tambakrejo", "nelayan@example.com", AdminUserRole.PENJUAL, false),
        ManagedUser("user-5", "Admin FishFlow", "admin@fishflow.app", AdminUserRole.ADMIN)
    )

    fun all(): List<ManagedUser> = synchronized(this) { users.toList() }

    fun count(): Int = synchronized(this) { users.size }

    fun toggleActive(userId: String): ManagedUser? = synchronized(this) {
        val index = users.indexOfFirst { it.id == userId }
        if (index < 0) return null
        users[index] = users[index].copy(isActive = !users[index].isActive)
        users[index]
    }

    fun resetToSeed() {
        synchronized(this) {
            users.clear()
            users.addAll(
                listOf(
                    ManagedUser("user-1", "Budi Santoso", "budi@example.com", AdminUserRole.WISATAWAN),
                    ManagedUser("user-2", "Siti Aminah", "siti@example.com", AdminUserRole.WISATAWAN),
                    ManagedUser("user-3", "Pak Slamet", "slamet@example.com", AdminUserRole.PENJUAL),
                    ManagedUser("user-4", "Nelayan Tambakrejo", "nelayan@example.com", AdminUserRole.PENJUAL, false),
                    ManagedUser("user-5", "Admin FishFlow", "admin@fishflow.app", AdminUserRole.ADMIN)
                )
            )
        }
    }
}

data class ManageUsersUiState(
    val users: List<ManagedUser> = emptyList(),
    val query: String = "",
    val roleFilter: AdminUserRole? = null,
    val isLoading: Boolean = true
) {
    val filteredUsers: List<ManagedUser>
        get() {
            val keyword = query.trim()
            return users.filter { user ->
                (roleFilter == null || user.role == roleFilter) &&
                    (
                        keyword.isBlank() ||
                            user.nama.contains(keyword, ignoreCase = true) ||
                            user.email.contains(keyword, ignoreCase = true)
                        )
            }
        }

    val activeCount: Int
        get() = users.count { it.isActive }
}

class ManageUsersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManageUsersUiState())
    val uiState: StateFlow<ManageUsersUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update {
            it.copy(users = AdminUserDirectory.all(), isLoading = false)
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onRoleFilterChange(role: AdminUserRole?) {
        _uiState.update {
            it.copy(roleFilter = if (it.roleFilter == role) null else role)
        }
    }

    fun onToggleActive(userId: String) {
        AdminUserDirectory.toggleActive(userId)
        load()
    }

    fun onResetFilter() {
        _uiState.update { it.copy(query = "", roleFilter = null) }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer { ManageUsersViewModel() }
        }
    }
}
