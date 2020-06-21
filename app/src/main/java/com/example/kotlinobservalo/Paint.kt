package com.example.kotlinobservalo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.util.*


object Paint {
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
    open fun getDominantColor(drawable: Drawable): Int {
        val bitmap = drawableToBitmap(drawable)
        if (bitmap == null) throw NullPointerException()
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height
        val pixels = IntArray(size)
        val bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false)
        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height)
        val colorMap = HashMap<Int, Int>()
        var color = 0
        var count: Int? = 0
        for (i in pixels.indices) {
            color = pixels[i]
            if (Color.alpha(color) > 5 && Math.abs(
                    Color.red(
                        color
                    ) - Color.green(color)
                ) + Math.abs(
                    Color.red(color) - Color.blue(
                        color
                    )
                ) + Math.abs(
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
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    var bitmap: Bitmap? = null
    if (drawable is BitmapDrawable) {
        val bitmapDrawable = drawable
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
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