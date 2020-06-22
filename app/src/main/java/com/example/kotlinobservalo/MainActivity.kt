package com.example.kotlinobservalo

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class MainActivity : AppCompatActivity() {

    var lista:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null

    var appWidth = 10
    var appHeight = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listaDeApps = AppGetter.getListaDeApps(this.applicationContext)

        lista = findViewById(R.id.lista)
        layoutManager = GridLayoutManager(this, 3, RecyclerView.HORIZONTAL, false)

        var itemDeco = ItemOffsetDecoration(10)

        lista?.addItemDecoration(itemDeco)
        //var snapHelper:PagerSnapHelper = PagerSnapHelper()
        //snapHelper.attachToRecyclerView(lista)

        appWidth = calcularAppWidth()
        appHeight = appWidth

        adaptador = AppAdapter(listaDeApps!!, appWidth, appHeight)

        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador
    }

    fun calcularAppWidth(): Int{
        val dispWidth = getDisplayContentSize('w')
        return dispWidth/3
    }

    //Busca la altura de la pantalla
    fun getDisplayContentSize(wh: Char): Int {
        val windowManager: WindowManager = getWindowManager()
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return if (wh == 'w') {
            //Saca el ancho de la pantalla
            size.x
        } else{
            var screenHeight = 0
            var actionBarHeight = 0
            var statusBarHeight = 0
            if (getActionBar() != null) {
                actionBarHeight = getActionBar()!!.getHeight()
            }

            //Saca parámetros del dispositivo
            val resourceId: Int =
                getResources().getIdentifier("status_bar_height", "dimensions", "android")

            //Si resourceId existe lo toma y calcula
            //la cantidad de píxeles de la pantalla
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId)
            }

            //Encuentra el borde superior
            //val contentTop: Int = Contexto.app.findViewById(android.R.id.content).getTop()

            //Saca el alto de la pantalla
            screenHeight = size.y
            screenHeight - actionBarHeight - statusBarHeight //- contentTop
        }
    }

    class ItemOffsetDecoration(private val mItemOffset: Int) : ItemDecoration() {
        constructor(context: Context, @DimenRes itemOffsetId: Int) : this(
            context.getResources().getDimensionPixelSize(itemOffsetId)
        ) {
        }
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect[mItemOffset, mItemOffset, mItemOffset] = mItemOffset
        }

    }

}



//mesi