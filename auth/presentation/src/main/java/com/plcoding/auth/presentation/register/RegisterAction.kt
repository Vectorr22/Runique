package com.plcoding.auth.presentation.register

sealed interface RegisterAction {
    data object onTogglePasswordVisibilityClick: RegisterAction
    data object onLoginclick: RegisterAction
    data object onRegisterClick: RegisterAction
}