package org.aossie.carbontracker.providers

import androidx.health.services.client.data.ExerciseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ExerciseStateHolder {
    private val _state = MutableStateFlow<ExerciseState?>(null)
    val state: StateFlow<ExerciseState?> = _state

    fun exerciseUpdate(newState: ExerciseState?) {
        val changed = newState != _state.value
        _state.value = newState
        if (changed) {
            _state.value = newState
        }
    }
}
