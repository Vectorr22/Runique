package com.plcoding.auth.presentation.login

sealed interface LoginAction {
    data object onTogglePasswordVisibility: LoginAction
    data object onLoginClicked: LoginAction
    data object onRegisterClicked: LoginAction
}