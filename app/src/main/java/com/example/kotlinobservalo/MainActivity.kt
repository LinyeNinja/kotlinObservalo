/*
Preguntas
¿Cómo funciona un theme?
error al volver de configuración a la main activity
error cuando intento agregar en un índice raro con mutablelist
*/

package com.example.kotlinobservalo

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils.indexOf
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.kotlinobservalo.Config.Configs
import com.google.android.material.snackbar.Snackbar

var alibaba = false

class MainActivity : AppCompatActivity() {

    var lista:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        //if (alibaba == false){
        //    getApplication().setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        //    alibaba = true
        //    reload()
        //}

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

        lista = findViewById(R.id.lista)

        if (Configs.obtenerBoolean("modoAltoContraste") == true){
            if (Configs.obtenerBoolean("modoNoche") == true){
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_highContrast_dark, null))
            }
            else{
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_highContrast_light, null))
            }
        }
        else{
            if (Configs.obtenerBoolean("modoNoche") == true){
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_dark, null))
                Log.d("a", ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_dark, null).toString())
            }
            else{
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_light, null))
            }
        }

        var listaDeApps: ArrayList<AppInfo>
        listaDeApps = AppGetter.getListaDeApps(this.applicationContext)
        val configAct = AppInfo("Configurar Launcher", "LclObservaloConfigActivity", null, Color.RED)
        listaDeApps.add(configAct)

        var mapDeIndices: MutableMap<String, Int> = mutableMapOf()

        var listaDeAppsAMostrar: MutableList<AppInfo>? = mutableListOf<AppInfo>()

        //acá a continuación se crea la lista de las apps que se van a mostrar utilizando los contenidos de ListaDeApps con los índices de listaDeAppsAMostrar
        //Esto tiene el problema de que si se elimina una aplicación esta no se va a borrar de mapDeIndices
        for (i in 1..listaDeApps.size-1){  //me parece que esto le saca una app... ups
            val element = listaDeApps[i]
            if (!(mapDeIndices.contains(listaDeApps[i].packageName))){
                mapDeIndices.put(listaDeApps[i].packageName, mapDeIndices.size+1)
            }
            listaDeAppsAMostrar?.add(mapDeIndices[listaDeApps[i].packageName]!!, element)
        }
        //listaDeAppsAMostrar.add(i, element)

        layoutManager = GridLayoutManager(this, cantFilas, RecyclerView.HORIZONTAL, false)

        var itemDeco = ItemOffsetDecoration(separacion)

        lista?.addItemDecoration(itemDeco)
        //var snapHelper:PagerSnapHelper = PagerSnapHelper()
        //snapHelper.attachToRecyclerView(lista)

        adaptador = AppAdapter(listaDeAppsAMostrar, appWidth, appHeight)

        //acá van las cosas sobre el modo de configuración del órden y demás:
        if (Configs.modoConfig == true){
            val itemTouchHelperCallback =
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP  or ItemTouchHelper.DOWN, 0) {
                    override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
                        val fromPos: Int = viewHolder.adapterPosition
                        val toPos: Int = target.adapterPosition
                        adaptador!!.swapItems(fromPos, toPos)
                        return true// true if moved, false otherwise
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        /*
                         noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))
                         Toast.makeText(
                             this@MainActivity,
                             getString(R.string.note_deleted),
                             Toast.LENGTH_SHORT
                         ).show()
                         */
                    }
                }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(lista)

            val fab: View = findViewById(R.id.fab)
            fab.setOnClickListener { view ->
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
                Log.d("a", "aaaaaaaaaaaaaaa")
            }
        }

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