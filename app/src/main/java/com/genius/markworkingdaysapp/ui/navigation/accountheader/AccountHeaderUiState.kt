package com.genius.markworkingdaysapp.ui.navigation.accountheader

sealed interface AccountHeaderUiState {

    data object SignedOut: AccountHeaderUiState

    data object SigningIn: AccountHeaderUiState

    data class SignedIn(
        val displayName: String?,
        val email: String?,
        val syncState: SyncIndicatorState,
    ) : AccountHeaderUiState

}