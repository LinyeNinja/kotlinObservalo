package com.example.kotlinobservalo.Botones

import android.graphics.Color.RED

class Btns(var label: String, color: Int = RED) {
    var color: Int = 0

    init {
        this.color = color
    }
}