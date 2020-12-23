package com.example.kotlinobservalo

/*
Esta clase tiene todas las declaraciones de los colores de tod0 lo de la aplicación y las funciones que las devuelven según el theme actual
Además, tiene toda función que se utilice para prosesamiento de imágenes y obtención de datos acerca de la pantalla
 */

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.Color.rgb
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.kotlinobservalo.Config.Configs
import java.util.*
import kotlin.math.abs

object Paint {

    var alturaDeLaPantalla = 0
    var anchuraDeLaPantalla = 0

    var appHeight = 0
    var appWidth = 0
    var separacion = 0

    var radio = 20f

    //acá van todas las funciones que entregan el color de cosas en la aplicación
    private val colorPrimary = rgb(0x62, 0x00, 0xEE)
    private val colorPrimaryDark = rgb(0x37,0x00,0xB3)
    private val colorAccent = rgb(0x03,0xDA,0xC5)

    private val colorAccept = rgb(0x77,0xDD,0x77)

    private val background_highContrast_light = rgb(0x0F,0x0F,0x0F)
    private val app_highContrast_light = rgb(0xF5,0xF5,0xF5)
    private val text_highContrast_light = rgb(0x0F,0x0F,0x0F)

    private val app_highContrast_dark = rgb(0x0F,0x0F,0x0F)
    private val background_highContrast_dark = rgb(0xF5,0xF5,0xF5)
    private val text_highContrast_dark = rgb(0xF5,0xF5,0xF5)

    private val background_dark = rgb(0x00,0x00,0x00)
    private val text_dark = argb(0xBF,0xFF,0xFF,0xFF)

    private val background_light = rgb(0xF5,0xF5,0xF5)
    private val text_light = argb(0xBF,0x00,0x00,0x00)

    private val app_fondo = rgb(0x0F,0x0F,0x0F)

    private val text_fondo_dark = argb(0xFF,0x00,0x00,0x00)
    private val text_fondo_light = argb(0xFF, 0xFF, 0xFF, 0xFF)

    private val carpetaAbierta_fondo_light = app_highContrast_light
    private val carpetaAbierta_fondo_dark = app_highContrast_dark

    private val carpeta_light =  argb( 0xFF, 0xFE, 0xE0, 0x98)
    private val carpeta_dark  =  argb( 0xFF, 0xFE, 0xE0, 0x98)

    fun colorAppLabel(): Int{
        if (Configs.obtenerBoolean("modoAltoContraste")){
            return if (Configs.obtenerBoolean("modoNoche")){
                text_highContrast_dark
            } else{
                text_highContrast_light
            }
        }
        else{
            return if (!Configs.obtenerBoolean("modoFondo")){
                if (Configs.obtenerBoolean("modoNoche")){
                    text_dark
                } else{
                    text_light
                }
            } else {
                if (Configs.obtenerBoolean("modoNoche")) {
                    text_fondo_dark
                } else {
                    text_fondo_light
                }
            }
        }
    }
    fun colorFondo(): Int{
        return if (Configs.obtenerBoolean("modoAltoContraste")){
            if (Configs.obtenerBoolean("modoNoche")){
                background_highContrast_dark
            } else{
                background_highContrast_light
            }
        } else{
            if (Configs.obtenerBoolean("modoNoche")) {
                background_dark
            } else {
                background_light
            }
        }
    }
    fun colorCarpetaAbierta(): Int{
        return if (Configs.obtenerBoolean("modoNoche")){
            carpetaAbierta_fondo_dark
        } else{
            carpetaAbierta_fondo_light
        }
    }
        fun colorCarpeta(): Int{
        var color: Int
        if (Configs.obtenerBoolean("modoAltoContraste")) {
            color = if (Configs.obtenerBoolean("modoNoche")) {
                app_highContrast_dark
            } else {
                app_highContrast_light
            }
        } else {
            color = if (Configs.obtenerBoolean("modoNoche")) {
                carpeta_dark
            } else {
                carpeta_light
            }
            if (Configs.obtenerBoolean("modoFondo")) {
                color = argb(200, color.red, color.green, color.blue)
            }
        }
        return color
    }

    fun colorApp(icon: Drawable): Int{
        var color: Int
        if (Configs.obtenerBoolean("modoAltoContraste")) {
            color = if (Configs.obtenerBoolean("modoNoche")) {
                app_highContrast_dark
            } else {
                app_highContrast_light
            }
        } else {
            color = if (Configs.obtenerBoolean("modoNoche")) {
                getDominantFlatColor(icon, "dark")
            } else {
                getDominantFlatColor(icon, "light")
            }
            if (Configs.obtenerBoolean("modoFondo")) {
                color = argb(200, color.red, color.green, color.blue)
            }
        }
        return color
    }
    fun colorObservaloApp(packageName: String): Int{
        var color: Int
        if (Configs.obtenerBoolean("modoAltoContraste")) {
            color = if (Configs.obtenerBoolean("modoNoche")) {
                app_highContrast_dark
            } else {
                app_highContrast_light
            }
        } else {
            color = when (packageName) {
                "Config" -> makeFlatColor(Color.GRAY)
                "Llamadas" -> makeFlatColor(Color.RED)
                "Lupa" -> makeFlatColor(Color.BLUE)
                "mensajes" -> makeFlatColor(rgb(0xFF, 0x88, 0x00))
                else -> makeFlatColor(Color.GRAY)
            }
            if (Configs.obtenerBoolean("modoNoche")) {
                color = app_highContrast_dark
            }
            if (Configs.obtenerBoolean("modoFondo")) {
                color = argb(200, color.red, color.green, color.blue)
            }
        }
        return color
    }

    /*
    fun getDominantColor(drawable: Drawable): Int {
        val bitmap = drawableToBitmap(drawable)

        val swatchesTemp: List<Palette.Swatch> = Palette.from(bitmap).generate().getSwatches()
        val swatches: List<Palette.Swatch> = ArrayList<Palette.Swatch>(swatchesTemp)
        Collections.sort(swatches, Comparator{ swatch1, swatch2 -> swatch2.population - swatch1.population })
        if (swatches.isNotEmpty()) {
            return swatches[0].getRgb()
        }
        else{
            return 0 //esto no gusta, debería ser el color por defecto que tira si algo male sal
        }
    }*/
    private fun getDominantColor(drawable: Drawable): Int {
        val bitmap = drawableToBitmap(drawable)
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height
        val pixels = IntArray(size)
        val bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false)
        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height)
        val colorMap = HashMap<Int, Int>()
        var color: Int
        var count: Int?
        for (i in pixels.indices) {
            color = pixels[i]
            if (Color.alpha(color) > 5 && abs(
                    Color.red(
                        color
                    ) - Color.green(color)
                ) + abs(
                    Color.red(color) - Color.blue(
                        color
                    )
                ) + abs(
                    Color.blue(color) - Color.green(
                        color
                    )
                ) > 150
            ) {
                count = colorMap[color]
                if (count == null) count = 0
                colorMap[color] = ++count
            }
        }
        var dominantColor = 0
        var max = 0
        for ((key, value) in colorMap) {
            if (value > max) {
                max = value
                dominantColor = key
            }
        }

        return dominantColor
    }

    private fun getDominantFlatColor(drawable: Drawable, mode: String): Int {
        return makeFlatColor(getDominantColor(drawable))
    }

    private fun makeFlatColor(color: Int): Int {
        var outColor: Int
        if (Configs.obtenerBoolean("modoAltoContraste")) {
            outColor = if (Configs.obtenerBoolean("modoNoche")) {
                app_highContrast_dark
            } else {
                app_highContrast_light
            }
        } else {
            if (Configs.obtenerBoolean("modoNoche")) {
                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)
                hsv[2] = 0.2f
                hsv[1] = 0.4f
                return Color.HSVToColor(hsv)
            } else {
                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)
                if(hsv[1] == 0f){ //si es negro/gris/blanco
                    hsv[2] = 0.7f
                    hsv[1] = 0f
                }
                else{
                    hsv[2] = 1f
                    hsv[1] = 0.4f
                }
                return Color.HSVToColor(hsv)
            }
        }
        if (Configs.obtenerBoolean("modoFondo")) {
            outColor = argb(200, color.red, color.green, color.blue)
        }
        return Color.RED
    }


    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap?
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}

