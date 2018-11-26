/*
 * Copyright (c) 2018 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.fire

import android.webkit.CookieManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


interface DuckDuckGoCookieManager {
    suspend fun removeExternalCookies()
}

class WebViewCookieManager(
    private val cookieManager: CookieManager,
    private val host: String
) : DuckDuckGoCookieManager {

    override suspend fun removeExternalCookies() {

        val ddgCookies = getDuckDuckGoCookies()

        suspendCoroutine<Unit> { continuation ->
            cookieManager.removeAllCookies {
                continuation.resume(Unit)
            }
        }

        storeDuckDuckGoCookies(ddgCookies)
    }

    private fun storeDuckDuckGoCookies(cookies: List<String>) {
        cookies.forEach {
            cookieManager.setCookie(host, it.trim())
        }
    }

    private fun getDuckDuckGoCookies(): List<String> {
        return cookieManager.getCookie(host)?.split(";").orEmpty()
    }
}