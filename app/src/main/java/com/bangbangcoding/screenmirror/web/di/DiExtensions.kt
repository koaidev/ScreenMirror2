package com.bangbangcoding.screenmirror.web.di

import android.content.Context
import com.bangbangcoding.screenmirror.web.KunApplication
import com.bangbangcoding.screenmirror.web.di.component.AppComponent

/**
 * The [AppComponent] attached to the application [Context].
 */
//val Context.injector: AndroidInjector<out DaggerApplication>
//    get() = (this.applicationContext as KunApplication).applicationInjector()

val Context.injector: AppComponent
    get() = KunApplication.appComponent


val injector: AppComponent
    get() = KunApplication.appComponent

/**
 * The [AppComponent] attached to the context, note that the fragment must be attached.
 */
//val Fragment.injector: AppComponent
//    get() = (context!!.applicationContext as KunApplication).applicationComponent

/**
 * The [AppComponent] attached to the context, note that the fragment must be attached.
 */
//@Suppress("DeprecatedCallableAddReplaceWith")
//@Deprecated("Consumers should switch to support.v4.app.Fragment")
//val android.app.Fragment.injector: AppComponent
//    get() = (activity!!.applicationContext as KunApplication).applicationComponent
