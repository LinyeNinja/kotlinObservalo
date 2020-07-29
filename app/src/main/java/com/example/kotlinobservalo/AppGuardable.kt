package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppGuardable(label:String, packageName:String, color: Int) {
    var label: String
    var packageName: String
    var color: Int = 0
    init {
        this.label = label
        this.packageName = packageName
        this.color = color
    }
}