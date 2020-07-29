/*
Preguntas
error al volver de configuración a la main activity
¿Es eficiente usar las referencias y pedir los íconos adentro del recyclerview?

El Gran Problema
No estoy pudiendo hacer que el recyclerview con las apps reciba los clicks del recyclerview del scroll.
Por alguna razón pasarlo solo cuando es click no funciona.
Hacerlo constantemente y deshabilitar el scrolling del otro tampoco (algunas soluciones no funcionan o hacen que no lo pueda scrollear manualmente, otras solo funcionan  si lo apreto por un milisegundo)
*/


package com.example.kotlinobservalo

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.kotlinobservalo.Config.Configs
import kotlinx.android.synthetic.main.app_equisemel.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    val snapear = false

    var listaSeparador:RecyclerView? = null
    var layoutManagerSeparador:RecyclerView.LayoutManager? = null
    var adaptadorSeparador:SeparadorAdapter? = null

    var lista:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        val tinydb = TinyDB(this)

        //if (alibaba == false){
        //    getApplication().setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        //    alibaba = true
        //    reload()
        //}

        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout: FrameLayout = findViewById(R.id.LinearLayout)

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
        listaSeparador = findViewById(R.id.listaSeparador)


        if (Configs.obtenerBoolean("modoAltoContraste") == true){
            if (Configs.obtenerBoolean("modoNoche") == true){
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_highContrast_dark, null))
            }
            else{
                lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_highContrast_light, null))
            }
        }
        else{
            if (Configs.obtenerBoolean("modoFondo") == true){
                val wm = WallpaperManager.getInstance(this)
                val d = wm.peekDrawable()
                layout.setBackground(d) // You can also use rl.setBackgroundDrawable(getWallpaper);
            }
            else {
                if (Configs.obtenerBoolean("modoNoche") == true) {
                    lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_dark, null))
                } else {
                    lista!!.setBackgroundColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.background_light, null))
                }
            }
        }


        var listaDeApps: ArrayList<AppInfo>
        listaDeApps = AppGetter.getListaDeApps(this.applicationContext)
        val configAct = AppInfo("Configurar Launcher", "LclObservaloConfigActivity", ContextCompat.getDrawable(this, R.drawable.config), Color.RED)
        listaDeApps.add(configAct)

        var listaDeAppsGuardadas = tinydb.getListaGuardada("list3")

        listaDeApps = listaGuardadaAListaAMostrarActualizando(listaDeAppsGuardadas, listaDeApps).toCollection(ArrayList())


        layoutManager = GridLayoutManager(this, cantFilas, RecyclerView.HORIZONTAL, false)
        layoutManager!!.canScrollHorizontally()

        var itemDeco = ItemOffsetDecoration(separacion)
        lista?.addItemDecoration(itemDeco)

        adaptador = AppAdapter(listaDeApps, appWidth, appHeight)


        layoutManagerSeparador = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
//        var itemDecoSeparador = ItemOffsetDecoration(separacion)
//        listaSeparador?.addItemDecoration(itemDecoSeparador)

        var snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(listaSeparador)

        var cantidadDeSeparadores: Float = listaDeApps.size.toFloat()
        cantidadDeSeparadores = ceil(cantidadDeSeparadores/cantFilas/cantColumnas)
        adaptadorSeparador = SeparadorAdapter(cantidadDeSeparadores.toInt(), getDisplayContentSize('w'), getDisplayContentSize('h'))

        //acá van las cosas sobre el modo de configuración del órden y demás:
        if (Configs.modoConfig == true){
            val fab: View = findViewById(R.id.fab)
            fab.setVisibility(View.VISIBLE)
            fab.setOnClickListener { view ->
                tinydb.putListaGuardada("list3", listaMostrableAListaGuardable(listaDeApps))
                terminarModoConfig()
            }

            val itemTouchHelperCallback =
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP  or ItemTouchHelper.DOWN, 0) {
                    override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
                        val fromPos: Int = viewHolder.adapterPosition
                        val toPos: Int = target.adapterPosition
                        adaptador!!.swapItems(fromPos, toPos)
                        return true //true if moved, false otherwise
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
        }
        else{
            val fab: View = findViewById(R.id.fab)
            fab.setVisibility(View.GONE)
        }

        lista!!.layoutManager = layoutManager
        lista!!.adapter = adaptador

        /*
        listaSeparador!!.addOnItemTouchListener(
            RecyclerTouchEvent( Contexto.mainActivity, listaSeparador,
                object : RecyclerTouchEvent.ClickListener {
                    override fun onClick(e: MotionEvent?)  {
                        lista!!.dispatchTouchEvent(e)
                    }
                })
        )*/

        listaSeparador!!.addOnItemTouchListener(PasadorDeClicks(lista!!))

        if (snapear == true) {
            listaSeparador?.layoutManager = layoutManagerSeparador
            listaSeparador?.adapter = adaptadorSeparador

            val scrollListeners = arrayOfNulls<RecyclerView.OnScrollListener>(2)
            scrollListeners[0] = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    //super.onScrolled(recyclerView, dx, dy)
                    //listaSeparador!!.removeOnScrollListener(scrollListeners[1]!!)
                    //listaSeparador!!.scrollBy(dx, dy)
                    //listaSeparador!!.addOnScrollListener(scrollListeners[1]!!)
                }
            }
            scrollListeners[1] = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    lista!!.removeOnScrollListener(scrollListeners[0]!!)
                    lista!!.scrollBy(dx, dy)
                    lista!!.addOnScrollListener(scrollListeners[0]!!)
                }
            }
            lista!!.addOnScrollListener(scrollListeners[0]!!)
            lista!!.setHasFixedSize(true)
            listaSeparador!!.addOnScrollListener(scrollListeners[1]!!)
        }

    }

    fun listaGuardadaAListaAMostrarActualizando(listaGuardada: ArrayList<AppGuardable>, listaDelCelu: ArrayList<AppInfo>): List<AppInfo> {
        var listaAMostrar = arrayOfNulls<AppInfo>(listaDelCelu.size)
        var cantidadDeAppsAAgregar = 0
        for (x in 0..listaDelCelu.size-1) { //¿Esto está bien?
            var existeEnGuardados = true
            for (y in 0..listaGuardada.size-1) { //¿Esto está bien?
                if(listaDelCelu[x].packageName == listaGuardada[y].packageName){
                    listaAMostrar.set(y, listaDelCelu[x])
                    existeEnGuardados = true
                }
            }
            if (existeEnGuardados == false){
                cantidadDeAppsAAgregar++
                listaAMostrar.set( (listaGuardada.size+cantidadDeAppsAAgregar), listaDelCelu[x] )
            }
        }
        return listaAMostrar.filterNotNull()
    }

    fun listaMostrableAListaGuardable(listaIn: ArrayList<AppInfo>): ArrayList<AppGuardable>{
        var listaNueva = ArrayList<AppGuardable>()
        for (i in 0..listaIn.size-1) { //¿Esto está bien?
            val appNueva = appMostrableAAppGuardable(listaIn[i])
            listaNueva.add(appNueva)
        }
        return listaNueva
    }

    fun appMostrableAAppGuardable(app: AppInfo): AppGuardable{
        return AppGuardable(app.label, app.packageName, app.color)
    }

    var accion: MotionEvent? = null
    class PasadorDeClicks(var recyclerAClickar: RecyclerView) : OnItemTouchListener {
        override fun onInterceptTouchEvent( rv: RecyclerView, e: MotionEvent ): Boolean {
            //opciones:
            if (e.action != MotionEvent.ACTION_MOVE) { //apreta el botón debajo del principio del scroll
            //if (e.action != MotionEvent.ACTION_MOVE && e.action != MotionEvent.ACTION_DOWN) { //no pasa click
            //if (e.action != MotionEvent.ACTION_MOVE && e.action == MotionEvent.ACTION_DOWN) { //no pasa click
            //if (e.action == MotionEvent.ACTION_DOWN) { //no pasa click
            //if (e.action == MotionEvent.ACTION_SCROLL) { //no pasa click
            //if (e.action == MotionEvent.ACTION_UP) { //no pasa click y corta el scroll del coso
                //val accionOriginal: MotionEvent = e
                //var accion: MotionEvent = e                 //
                //accion.action = MotionEvent.ACTION_DOWN     //esta línea y la de arriba por alguna razón hacen que no funcione el scroll
                recyclerAClickar.dispatchTouchEvent(e)
                //e.action = accionOriginal.action            //esta línea no soluciona el último comentario
                //return true //esto solo hace que se corte el scroll con algunos
            }
            return false
        }
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    class RecyclerViewDisabler : OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val action = e.action
            if (e.action == MotionEvent.ACTION_MOVE) {
                return true
            } else {
                return false
            }
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    /*
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
    */

    fun terminarModoConfig(){
        Configs.modoConfig = false
        AppGetter.launch("LclObservaloConfigActivity")
    }

    override fun onBackPressed() {
        if(Configs.modoConfig == true){
            terminarModoConfig()
        }
        else{

        }
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