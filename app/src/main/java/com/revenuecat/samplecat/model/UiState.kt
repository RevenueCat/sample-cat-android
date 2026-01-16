package com.revenuecat.samplecat.model

/**
 * Represents the UI state for asynchronous operations.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

/**
 * Represents the state of a purchasable item.
 */
enum class PurchasableState {
    ReadyToPurchase,
    Purchasing,
    Purchased,
    PurchasingOtherProduct
}
