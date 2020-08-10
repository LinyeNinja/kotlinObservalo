package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppGuardable(listaCarpeta: MutableList<AppInfo>? = null, label:String, packageName:String, color: Int) {
    var listaCarpeta: MutableList<AppInfo>? = null
    var label: String
    var packageName: String
    var color: Int = 0

    init {
        this.listaCarpeta = listaCarpeta
        this.label = label
        this.packageName = packageName
        this.color = color
    }
}