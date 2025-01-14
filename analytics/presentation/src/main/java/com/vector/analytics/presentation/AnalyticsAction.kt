package com.vector.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackCLick: AnalyticsAction
}