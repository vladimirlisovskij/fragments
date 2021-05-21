package com.example.myapplication.contextHolder

import android.content.Context
import dagger.Module
import dagger.Provides

class ContextHolder(
    private val context: Context
){
    fun getContext() : Context {
        return context
    }
}

@Module
class ContextModule (private val context: Context) {
    @Provides
    fun getContext() : ContextHolder = ContextHolder(context)
}