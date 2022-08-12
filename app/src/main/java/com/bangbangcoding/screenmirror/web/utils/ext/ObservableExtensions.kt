package com.bangbangcoding.screenmirror.web.utils.ext

import io.reactivex.Observable

/**
 * Filters the [Observable] to only instances of [T].
 */
inline fun <reified T> Observable<out Any>.filterInstance(): Observable<T> {
    return this.filter { it is T }.map { it as T }
}
