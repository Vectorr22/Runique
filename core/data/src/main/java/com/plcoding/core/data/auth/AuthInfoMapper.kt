package com.plcoding.core.data.auth

import com.plcoding.core.domain.AuthInfo

fun AuthInfo.toAuthInfoSerializable(): AuthInfoSerializable{
    return AuthInfoSerializable(
        accesToken = accessToken,
        refreshToken = refreshToken,
        userId = userId
    )
}

fun AuthInfoSerializable.toAuthInfo(): AuthInfo{
    return AuthInfo(
        accessToken = accesToken,
        refreshToken = refreshToken,
        userId = userId
    )
}