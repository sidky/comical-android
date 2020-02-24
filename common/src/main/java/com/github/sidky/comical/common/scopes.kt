package com.github.sidky.comical.common

import javax.inject.Qualifier
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class LoggedInScope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureScope
