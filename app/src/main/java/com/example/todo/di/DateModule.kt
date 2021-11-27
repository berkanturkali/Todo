package com.example.todo.di

import com.example.todo.di.qualifier.DateFormatQualifiers
import com.example.todo.di.qualifier.DateTimeFormatQualifiers
import com.example.todo.di.qualifier.TimeFormatQualifiers
import com.example.todo.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*

@Module
@InstallIn(SingletonComponent::class)
object DateModule {

    @Provides
    @DateFormatQualifiers
    fun provideDateFormat() = SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault())

    @Provides
    @DateTimeFormatQualifiers
    fun provideDateTimeFormat() = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault())

    @Provides
    @TimeFormatQualifiers
    fun provideTimeFormat(): SimpleDateFormat {
        val format = SimpleDateFormat(Constants.TIME_PATTERN,Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC+03")
        return format
    }
}