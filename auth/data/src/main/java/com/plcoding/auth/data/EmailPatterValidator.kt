package com.plcoding.auth.data

import android.util.Patterns
import com.plcoding.auth.domain.PatternValidator

object EmailPatterValidator: PatternValidator {
    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}