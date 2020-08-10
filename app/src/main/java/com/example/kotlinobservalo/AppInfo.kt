package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.drawable.Drawable

class AppInfo(listaCarpeta: MutableList<AppInfo>? = null, label:String, packageName:String = "null", icon:Drawable? = null, color: Int = RED) {
    var listaCarpeta: MutableList<AppInfo>? = null
    var label: String
    var packageName: String
    var icon: Drawable?
    var color: Int = 0

    init {
        this.listaCarpeta = listaCarpeta
        this.label = label
        this.packageName = packageName
        this.icon = icon
        this.color = color
    }
}