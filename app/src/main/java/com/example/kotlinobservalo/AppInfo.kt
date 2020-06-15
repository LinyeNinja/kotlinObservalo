package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppInfo(label:String, packageName:String, icon:Int) {
    lateinit var label: String
    lateinit var packageName: String
    var icon = 0

    init {
        this.label = label
        this.packageName = packageName
        this.icon = icon
    }
}