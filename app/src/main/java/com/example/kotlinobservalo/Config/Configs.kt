package com.example.kotlinobservalo.Config

import androidx.preference.PreferenceManager
import com.example.kotlinobservalo.Contexto

object Configs {

    val prefs = PreferenceManager.getDefaultSharedPreferences(Contexto.mainActivity)

    private var modoAltoContraste = false
    private var cantColumnas = 3
    private var modoGamer = false
    private var puedeRotar = false

    var cambiado = false

    var modoConfig = false

    fun obtenerBoolean(input: String):Boolean{
        return prefs.getBoolean(input, false)
    }

    fun modoAltoContraste(input: Boolean? = null):Boolean {
        if (input == null) {
            return prefs.getBoolean("modoAltoContraste", false)
        }
        else if (input == false){
            val editor = prefs.edit()
            editor.putBoolean("modoAltoContraste", true)
            editor.apply()
        }
        else if (input == true){
            val editor = prefs.edit()
            editor.putBoolean("modoAltoContraste", true)
            editor.apply()
        }
        cambiado = true
        return false
    }

    fun puedeRotar(input: Boolean? = null):Boolean {
        if (input == null) {
            return prefs.getBoolean("puedeRotar", false)
        }
        else if (input == false){
            val editor = prefs.edit()
            editor.putBoolean("puedeRotar", true)
            editor.apply()
        }
        else if (input == true){
            val editor = prefs.edit()
            editor.putBoolean("puedeRotar", true)
            editor.apply()
        }
        cambiado = true
        return false
    }

    fun cantColumnas(input: Int = 0):Int {
        if (input != 0) {
            val editor = prefs.edit()
            editor.putInt("cantColumnas", input)
            editor.apply()
        }
        else{
            return prefs.getInt("cantColumnas", 3)
        }
        cambiado = true
        return 0
    }

}