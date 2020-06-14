package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppInfo {
    lateinit var label: String
    lateinit var packageName: String
    lateinit var icon: Drawable

    init {
        this.label = label
        this.packageName = packageName
        this.icon = icon
    }
}