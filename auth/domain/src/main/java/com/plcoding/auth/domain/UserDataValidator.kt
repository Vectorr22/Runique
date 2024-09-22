package com.plcoding.auth.domain

class UserDataValidator(
    private val patternValidator: PatternValidator
) {
    fun isValidEmail(email: String): Boolean {
        return patternValidator.matches(email.trim())
    }

    fun isValidPassword(password: String): PasswordValidationState {
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasNumber = password.any{ it.isDigit() }

        return PasswordValidationState(
            hasNumber = hasNumber,
            hasLowerCaseCharacter = hasLowerCase,
            hasUpperCaseCharacter = hasUpperCase,
            hasMinLength = hasMinLength
        )
    }


    companion object {
        const val MIN_PASSWORD_LENGTH = 9
    }
}