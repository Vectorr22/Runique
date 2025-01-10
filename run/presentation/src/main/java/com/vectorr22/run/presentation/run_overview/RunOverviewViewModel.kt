package com.vectorr22.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.core.domain.run.RunRepository
import com.vectorr22.run.presentation.run_overview.mappers.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository
): ViewModel() {
    var state by mutableStateOf(RunOverviewState())
        private set
    init {
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(
                runs = runsUi
            )
        }.launchIn(viewModelScope)
        viewModelScope.launch {
            runRepository.fetchRuns()
        }

    }
    fun onAction(action: RunOverviewAction){
        when(action){
            RunOverviewAction.onStartClick -> Unit
            RunOverviewAction.onLogoutClick -> Unit
            is RunOverviewAction.DeleteRun ->{
                viewModelScope.launch {
                    runRepository.deleteRun(action.run.id)
                }
            }
            else -> Unit
        }
    }
}