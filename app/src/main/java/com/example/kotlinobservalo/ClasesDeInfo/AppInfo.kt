package com.example.kotlinobservalo.ClasesDeInfo

import android.graphics.Color.RED
import android.graphics.drawable.Drawable

class AppInfo(listaCarpeta: MutableList<AppInfo>? = null, label:String, packageName:String = "null", icon:Drawable? = null, color: Int = RED, visible: Boolean = true) {
    var listaCarpeta: MutableList<AppInfo>? = null
    var label: String
    var packageName: String
    var icon: Drawable?
    var color: Int = 0
    var visible: Boolean

    init {
        this.listaCarpeta = listaCarpeta
        this.label = label
        this.packageName = packageName
        this.icon = icon
        this.color = color
        this.visible = visible
    }
}