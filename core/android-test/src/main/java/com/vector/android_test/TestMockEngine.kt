@file:OptIn(InternalAPI::class)

package com.vector.android_test

import com.plcoding.auth.data.model.LoginResponse
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.util.InternalAPI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext



val loginResponseStub = LoginResponse(
    accessToken = "test-access-token",
    refreshToken = "test-refresh-token",
    accessTokenExpirationTimestamp = System.currentTimeMillis(),
    userId = "test-user-id"
)

class TestMockEngine(
    override val dispatcher: CoroutineDispatcher,
    private val mockEngineConfig: MockEngineConfig
): HttpClientEngine {

    val mockEngine = MockEngine(mockEngineConfig)
    override val coroutineContext: CoroutineContext
        get() = mockEngine.coroutineContext + dispatcher

    override val config: HttpClientEngineConfig
        get() = mockEngineConfig

    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        return withContext(coroutineContext) {
            mockEngine.execute(data)
        }
    }

    override fun close() {
        mockEngine.close()
    }

}