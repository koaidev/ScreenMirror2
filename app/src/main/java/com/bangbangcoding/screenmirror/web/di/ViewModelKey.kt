package com.bangbangcoding.screenmirror.web.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by ThaoBKN on 25/11/2021
 */

@Target(allowedTargets = [AnnotationTarget.FUNCTION])
@Retention(value = AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)