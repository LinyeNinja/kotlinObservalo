package com.example.kotlinobservalo.Botones

import android.graphics.Color.RED

class Btns(label:String, color: Int = RED) {
    var label: String
    var color: Int = 0

    init {
        this.label = label
        this.color = color
    }
}