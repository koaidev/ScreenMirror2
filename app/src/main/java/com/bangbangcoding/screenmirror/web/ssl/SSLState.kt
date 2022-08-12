package com.bangbangcoding.screenmirror.web.ssl

import android.net.http.SslError

sealed class SSLState {

    object None : SSLState()

    object Valid : SSLState()

    class Invalid(val sslError: SslError) : SSLState()

}