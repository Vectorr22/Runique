package com.vectorr22.run.presentation.run_overview

import androidx.lifecycle.ViewModel

class RunOverviewViewModel: ViewModel() {

    fun onAction(action: RunOverviewAction){
        when(action){
            RunOverviewAction.onAnalyticsClick -> TODO()
            RunOverviewAction.onLogoutClick -> TODO()
            else -> Unit
        }
    }
}