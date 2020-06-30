package com.example.kotlinobservalo

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.kotlinobservalo.Config.Configs
import java.util.stream.Collectors.toList


class MainActivity : AppCompatActivity() {

    var lista:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tinydb = TinyDB(this)

        var appWidth = 10
        var appHeight = 10
        var separacion = 5
        var cantFilas = 0
        var cantColumnas = Configs.cantColumnas()

        appWidth = getDisplayContentSize('w')/cantColumnas - separacion*2
        appHeight = appWidth

        cantFilas = getDisplayContentSize('h') / (appHeight + separacion)
        var resto: Int = getDisplayContentSize('h') % (appHeight + separacion) //obtiene el espacio restante blanco
        if (resto > 0.7 * appHeight) {                                                    //se fija si este espacio es muy grande
            resto = appHeight - resto //si lo es, calcula el espacio que le falta para que haya un boton más
            appHeight -= resto / cantFilas //achica a todos los botones para que entre una fila más
            cantFilas++ //y agrega una fila más
        }

        var listaDeApps: ArrayList<AppInfo>
        listaDeApps = AppGetter.getListaDeApps(this.applicationContext)
        val configAct = AppInfo("Configurar Launcher", "LclObservaloConfigActivity", null, Color.RED)
        listaDeApps.add(configAct)

        tinydb.putListObject("listaDeApps", listaDeApps);

        //var listaDeAppsLocales = listaDeActivitiesLocales()
        //listaDeApps?.addAll(listaDeAppsLocales!!)

        lista = findViewById(R.id.lista)

        layoutManager = GridLayoutManager(this, cantFilas, RecyclerView.HORIZONTAL, false)

        var itemDeco = ItemOffsetDecoration(separacion)

        lista?.addItemDecoration(itemDeco)
        //var snapHelper:PagerSnapHelper = PagerSnapHelper()
        //snapHelper.attachToRecyclerView(lista)

        adaptador = AppAdapter(listaDeApps, appWidth, appHeight)

        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador
    }

    override fun onResume(){
        super.onResume()
        if (Configs.cambiado == true){
            Configs.cambiado = false
            reload()
        }
    }

    fun reload(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
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