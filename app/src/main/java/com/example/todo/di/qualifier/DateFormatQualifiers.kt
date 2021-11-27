package com.example.todo.di.qualifier

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DateFormatQualifiers

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DateTimeFormatQualifiers

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class TimeFormatQualifiers