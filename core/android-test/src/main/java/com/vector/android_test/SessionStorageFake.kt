package com.vector.android_test

import com.plcoding.core.domain.AuthInfo
import com.plcoding.core.domain.SessionStorage

class SessionStorageFake:SessionStorage {

    private var authInfo: AuthInfo? = null

    override suspend fun get(): AuthInfo? {
        return authInfo
    }

    override suspend fun set(authInfo: AuthInfo?) {
        this.authInfo = authInfo
    }
}