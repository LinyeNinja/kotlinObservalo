package com.example.kotlinobservalo.ClasesDeInfo

import android.graphics.Color
import android.graphics.drawable.Drawable

class AppGuardable(listaCarpeta: Int? = null, label:String, packageName:String, color: Int, visible:Boolean = true) {
    var listaCarpeta: Int? = null
    var label: String
    var packageName: String
    var color: Int = 0
    var visible: Boolean

    init {
        this.listaCarpeta = listaCarpeta
        this.label = label
        this.packageName = packageName
        this.color = color
        this.visible = visible
    }
}