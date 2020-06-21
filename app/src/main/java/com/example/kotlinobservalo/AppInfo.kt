package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppInfo(label:String, packageName:String, icon:Drawable, color: Int) {
    lateinit var label: String
    lateinit var packageName: String
    lateinit var icon: Drawable
    var color: Int = 0

    init {
        this.label = label
        this.packageName = packageName
        this.icon = icon
        this.color = color
    }
}