package com.bangbangcoding.screenmirror.web.utils

import androidx.annotation.IntDef
import com.bangbangcoding.screenmirror.web.utils.Constants.NO_PROXY
import com.bangbangcoding.screenmirror.web.utils.Constants.PROXY_I2P
import com.bangbangcoding.screenmirror.web.utils.Constants.PROXY_MANUAL
import com.bangbangcoding.screenmirror.web.utils.Constants.PROXY_ORBOT


/**
 * Proxy choice integer definition.
 *
 * These should match the order of @array/proxy_choices_array
 */
@IntDef(NO_PROXY, PROXY_ORBOT, PROXY_I2P, PROXY_MANUAL)
@Retention(AnnotationRetention.SOURCE)
annotation class Proxy
