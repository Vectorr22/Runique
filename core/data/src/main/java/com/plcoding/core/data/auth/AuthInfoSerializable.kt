package com.plcoding.core.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthInfoSerializable(
    val accesToken: String,
    val refreshToken: String,
    val userId: String
)
