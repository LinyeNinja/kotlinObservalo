

package com.example.kotlinobservalo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.example.kotlinobservalo.ClasesDeInfo.AppGuardable
import com.example.kotlinobservalo.ClasesDeInfo.AppInfo
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Contexto.app
import com.example.kotlinobservalo.Paint.appHeight
import com.example.kotlinobservalo.Paint.appWidth
import java.lang.Math.abs
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

    public lateinit var layout: FrameLayout

    lateinit var listaDeApps: ArrayList<AppInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        Paint.alturaDeLaPantalla = getDisplayContentSize('h')
        Paint.anchuraDeLaPantalla = getDisplayContentSize('w')

        val tinydb = TinyDB(this)

        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO esto no funciona ¿razón?
/*
        val scalingFactor = 0.5f // scale down to half the size
        View.setScaleX(scalingFactor)
        View.setScaleY(scalingFactor)
*/
        when {
            intent?.action == Intent.ACTION_PACKAGE_CHANGED -> {
                Log.d("aaa","AAAAAAAAAAAAAAAAAAAAAAAAAAa")
                reload()
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }

        layout = findViewById(R.id.LinearLayout)

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
        /*for (i:Int in 0..11) {
            lista!!.recycledViewPool.setMaxRecycledViews(0, 0)
        }*/
            if (Configs.obtenerBoolean("modoFondo") == true){
                val wm = WallpaperManager.getInstance(this)
                val d = wm.peekDrawable()
                layout.setBackground(d) // You can also use rl.setBackgroundDrawable(getWallpaper);
            }
            else {
                lista!!.setBackgroundColor(Paint.colorFondo())
            }

        listaDeApps = AppGetter.getListaDeApps(this.applicationContext)

        val configAct = AppInfo(
            null,
            "Configurar Launcher",
            "LclObservaloConfigActivity",
            ContextCompat.getDrawable(this, R.drawable.config),
            Color.RED
        )
        listaDeApps.add(configAct)

        val llamadasAct = AppInfo(
            null,
            "Llamadas",
            "LclObservaloLlamadasActivity",
            ContextCompat.getDrawable(this, R.drawable.config),
            Color.RED
        )
        listaDeApps.add(llamadasAct)

        val lupaAct = AppInfo(
            null,
            "Lupa",
            "LclObservaloLupa",
            ContextCompat.getDrawable(this, R.drawable.config),
            Color.RED
        )
        listaDeApps.add(lupaAct)

        var listaDeAppsGuardadas = tinydb.getListaGuardada("list3")

        listaDeApps = listaGuardadaAListaAMostrarActualizando(listaDeAppsGuardadas, listaDeApps).toCollection(ArrayList())

        var listaDeInvisibles = getListaDeInvisibles(listaDeApps)
        listaDeApps = purgeInvisibles(listaDeApps)

        layoutManager = GridLayoutManager(this, cantFilas, RecyclerView.HORIZONTAL, false)
        layoutManager!!.canScrollHorizontally()

        var itemDeco = ItemOffsetDecoration(separacion)
        lista!!.addItemDecoration(itemDeco)

        fun onConfigHappenedInAppAdapter(evento: String, packageName: String){
            if (evento == "appEscondida"){
                ocultarUnaAppYNotificarAlRecycler(null, packageName)
            }
            else if (evento == "appDesinstalada"){
                desinstalarUnaAppYNotificarAlRecycler(null, packageName)
            }
            else if (evento == "appSacadaDeCarpeta"){
                sacarUnaAppDeCarpetaYNotificarAlRecycler(packageName)
            }
            else if (evento.contains("tituloCarpetaEditada")){
                val position = evento.filter { it.isDigit() }
                cambiarNombreCarpeta(position.toInt(), packageName)
            }
        }

        adaptador = AppAdapter(listaDeApps){ evento, pos -> onConfigHappenedInAppAdapter(evento, pos) }

        //acá van las cosas sobre el modo de configuración del órden y demás:
        val fab: View = findViewById(R.id.fab)
        val borrarOCarpeta: View = findViewById(R.id.borrarOCarpeta)
        if (Configs.modoConfig == true){
            if (listaDeInvisibles.size > 0){
                listaDeApps.addAll(listaDeInvisibles)
            }

            fab.visibility = View.VISIBLE
            fab.setOnClickListener { view ->
                tinydb.putListaGuardada("list3", listaMostrableAListaGuardable(listaDeApps))
                terminarModoConfig()
            }

            borrarOCarpeta.visibility = View.INVISIBLE
            val duracionAnimacionBotones = 500.toLong()
            val borrarOCarpeta_borrar = borrarOCarpeta.findViewById<Button>(R.id.borrar)
            val borrarOCarpeta_carpeta = borrarOCarpeta.findViewById<Button>(R.id.carpeta)
            var x = 0f
            var y = 0f
            var enBotonBorrar = false
            var enBotonCarpeta = false
            var appAgarrada: Int? = null
            var carpetaDestino: Int? = null

            lista!!.setOnTouchListener(OnTouchListener { v, event ->
                if (borrarOCarpeta.visibility == VISIBLE) {
                    x = event.x
                    y = event.y
                    if (y < borrarOCarpeta_borrar.bottom && x < borrarOCarpeta_borrar.right && x > borrarOCarpeta_borrar.left) { //está hoveriando sobre borrar
                        enBotonBorrar = true
                    }else{
                        enBotonBorrar = false
                    }
                    if (y < borrarOCarpeta_carpeta.bottom && x < borrarOCarpeta_carpeta.right && x > borrarOCarpeta_carpeta.left) { //está hoveriando sobre carpeta
                        enBotonCarpeta = true
                    }else{
                        enBotonCarpeta = false
                    }
                }
                false
            })

            val itemTouchHelperCallback =
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP  or ItemTouchHelper.DOWN, 0) {
                    override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
                        val fromPos: Int = viewHolder.adapterPosition
                        val toPos: Int = target.adapterPosition

                        appAgarrada = null

                        if(listaDeApps[toPos].packageName == "carpeta"){    //si lo tira a una carpeta
                            if(appAgarrada == null){
                                appAgarrada = fromPos
                            }
                            carpetaDestino = toPos
                        } else {                                            //si lo tira a una posición común:
                            appAgarrada = toPos
                            carpetaDestino = null
                            if (fromPos < toPos) {
                                for (i in fromPos until toPos) {
                                    Collections.swap(listaDeApps, i, i + 1)
                                }
                            } else {
                                for (i in fromPos downTo toPos + 1) {
                                    Collections.swap(listaDeApps, i, i - 1)
                                }
                            }
                            //notifyItemMoved(fromPosition, toPosition)
                            adaptador!!.notifyItemMoved(fromPos, toPos)
                        }
                        return true //true if moved, false otherwise
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        /*
                         noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))
                         */
                    }

                    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                        super.onSelectedChanged(viewHolder, actionState)
                        when (actionState) {
                            ItemTouchHelper.ACTION_STATE_DRAG -> { //cuando se empieza a mover la app agarrada
                                viewHolder?.also { appAgarrada = it.adapterPosition }
                                if(borrarOCarpeta.visibility != VISIBLE) {
                                    borrarOCarpeta.setAlpha(0f);
                                    borrarOCarpeta.visibility = VISIBLE
                                    borrarOCarpeta.animate()
                                        .alpha(1f)
                                        .setDuration(duracionAnimacionBotones)
                                        .setListener(null);
                                }
                            }
                            ItemTouchHelper.ACTION_STATE_IDLE -> { //cuando se suelta la app agarrada
                                //if (carpetaDestino != null && appAgarrada != null && listaDeApps[carpetaDestino!!].listaCarpeta != null){ //si se está tirando sobre una carpeta}
                                if(carpetaDestino != null && appAgarrada != null){
                                    try {
                                        listaDeApps[carpetaDestino!!].listaCarpeta!!.add(listaDeApps[appAgarrada!!])
                                        listaDeApps.removeAt(appAgarrada!!)
                                        adaptador!!.notifyDataSetChanged()
                                        borrarOCarpeta.animate()
                                        .alpha(0f)
                                        .setDuration(duracionAnimacionBotones)
                                        .setListener(object : AnimatorListenerAdapter() {
                                            override fun onAnimationEnd(animation: Animator?) {
                                                borrarOCarpeta.setVisibility(GONE)
                                            }
                                        })
                                    } catch(e: Exception){
                                        Log.d("ERRORappAgarrada:", listaDeApps[appAgarrada!!].label)
                                        Log.d("ERRORcarpetaDestino:", listaDeApps[carpetaDestino!!].label)
                                    }
                                }
                                else{
                                if(borrarOCarpeta.visibility == VISIBLE) {
                                    if(enBotonBorrar && appAgarrada != null){
                                        ocultarUnaAppYNotificarAlRecycler(appAgarrada)
                                    }
                                    if(enBotonCarpeta && appAgarrada != null){
                                        //crear una carpeta con la app seleccionada:
                                        val listaDeCarpetaNueva = ArrayList<AppInfo>()
                                        listaDeCarpetaNueva.add(listaDeApps[appAgarrada!!])
                                        listaDeApps.removeAt(appAgarrada!!)

                                        val carpetaNueva =
                                            AppInfo(
                                                listaDeCarpetaNueva,
                                                "carpeta nueva",
                                                "carpeta",
                                                null,
                                                Color.RED
                                            )

                                        listaDeApps.add(appAgarrada!!, carpetaNueva)

                                        adaptador!!.notifyDataSetChanged()
                                    }
                                    borrarOCarpeta.animate()
                                        .alpha(0f)
                                        .setDuration(duracionAnimacionBotones)
                                        .setListener(object : AnimatorListenerAdapter() {
                                            override fun onAnimationEnd(animation: Animator?) {
                                                borrarOCarpeta.setVisibility(GONE)
                                            }
                                        })
                                }
                                }
                            }
                        }
                    }
                }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(lista)
        }
        else{
            fab.visibility = View.GONE
            borrarOCarpeta.visibility = View.GONE
        }

        lista!!.layoutManager = layoutManager
        lista!!.adapter = adaptador


        if (snapear == true) {
            listaSeparador = findViewById(R.id.listaSeparador)

            layoutManagerSeparador = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)

            var snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(listaSeparador)

            var cantidadDeSeparadores: Float = listaDeApps.size.toFloat()
            cantidadDeSeparadores = ceil(cantidadDeSeparadores/cantFilas/cantColumnas)
            adaptadorSeparador = SeparadorAdapter(cantidadDeSeparadores.toInt(), getDisplayContentSize('w'), getDisplayContentSize('h'))

            //clicks:
            //listaSeparador!!.addOnItemTouchListener(PasadorDeClicks(lista!!))

            /*
            listaSeparador!!.setOnClickListener { view -> //esto hace que no funcione
                //sin eso pasa tod0
                listaSeparador!!.addOnItemTouchListener(
                    RecyclerTouchEvent( Contexto.mainActivity, listaSeparador,
                        object : RecyclerTouchEvent.ClickListener {
                            override fun onClick(e: MotionEvent?)  {
                                lista!!.dispatchTouchEvent(e)
                            }
                        })
                )
            }
            */

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

    //A CONTINUACIÓN, FUNCIONES QUE MODIFICAN A LA LISTA DE APPS Y AL RECYCLERVIEW, POR LO QUE SOLO DEBERÍAN LLAMARSE CON UNO BIEN ACTIVO Y DEMÁS//
    fun ocultarUnaAppYNotificarAlRecycler(posApp: Int?, packageName: String? = null){
        if (posApp != null){ //si se quiere hacer mediante la posición en el recycler
            listaDeApps[posApp].visible = false
            listaDeApps.add(listaDeApps[posApp])
            listaDeApps.removeAt(posApp)
        }
        else{ //si se quiere hacer mediante el nombre )
            for (i in 0..listaDeApps.size-1) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0..listaCarpeta!!.size - 1) {
                        if(listaCarpeta[j].packageName == packageName){
                            listaCarpeta[j].visible = false
                            listaDeApps.add(listaCarpeta[j])
                            listaCarpeta.removeAt(j)
                            if (listaDeApps[i].listaCarpeta!!.size == 0){
                                listaDeApps.removeAt(i)
                            }
                        }
                    }
                }
            }
        }
        adaptador!!.notifyDataSetChanged()
    }
    fun desinstalarUnaAppYNotificarAlRecycler(posApp: Int? = null, packageName: String){
        var nombreDelPaquete = packageName
        if (posApp != null){ //si se quiere hacer mediante la posición en el recycler
            listaDeApps[posApp].visible = false
            listaDeApps.add(listaDeApps[posApp])
            listaDeApps.removeAt(posApp)
            nombreDelPaquete = listaDeApps[posApp].packageName
        }
        else{ //si se quiere hacer mediante el nombre )
            for (i in 0..listaDeApps.size-1) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0..listaCarpeta!!.size - 1) {
                        if(listaCarpeta[j].packageName == nombreDelPaquete){
                            listaCarpeta.removeAt(j)
                            if (listaDeApps[i].listaCarpeta!!.size == 0){
                                listaDeApps.removeAt(i)
                            }
                        }
                    }
                }
                if(listaDeApps[i].packageName == nombreDelPaquete){
                    listaDeApps.removeAt(i)
                }
            }
        }
        adaptador!!.notifyDataSetChanged()
        AppGetter.uninstall(nombreDelPaquete)
    }
    fun cambiarNombreCarpeta(position: Int, label: String){
        listaDeApps[position].label = label
        adaptador!!.notifyDataSetChanged()
    }


    fun sacarUnaAppDeCarpetaYNotificarAlRecycler(packageName: String? = null){
            for (i in 0..listaDeApps.size-1) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0..listaCarpeta!!.size - 1) {
                        if(listaCarpeta[j].packageName == packageName){
                            listaDeApps.add(i+1, listaCarpeta[j])
                            listaDeApps[i].listaCarpeta!!.removeAt(j)
                            if (listaDeApps[i].listaCarpeta!!.size == 0){
                                listaDeApps.removeAt(i)
                            }
                        }
                    }
                }
            }
        adaptador!!.notifyDataSetChanged()
    }
    //FIN DE FUNCIONES QUE MODIFICAN A LA LISTA DE APPS Y AL RECYCLERVIEW//

    fun getListaDeInvisibles(listaIn: ArrayList<AppInfo>): ArrayList<AppInfo>{
        val listaOut = ArrayList<AppInfo>()
        for (i in 0..listaIn.size-1){
            if(listaIn[i].visible == false){
                listaOut.add(listaIn[i])
            }
        }
        return listaOut
    }

    fun purgeInvisibles(listaIn: ArrayList<AppInfo>): ArrayList<AppInfo>{
        for (i in listaIn.size-1 downTo 0){ //TODO ese 2 me da miedo
            if(listaIn[i].visible == false){
                listaIn.removeAt(i)
            }
            else{
            }
        }
        return listaIn
    }

    fun listaGuardadaAListaAMostrarActualizando(listaGuardada: ArrayList<AppGuardable>, listaDelCelu: ArrayList<AppInfo>): List<AppInfo> {
        var listaAMostrar =
            arrayOfNulls<AppInfo>(listaDelCelu.size + 5) //TODO OH GOD, ESTO ESTÁ TOTALMENTE MAL Y ES TERRIBLEMENTE INEFICIENTE
        var cantidadDeAppsAAgregar = 0
        var carpetaAgregandoActual = 0
        var cantidadDeCarpetasAgregadas = 0

        val listaDeCarpetas = ArrayList<ArrayList<Int>>()
        //encontrar las carpetas:
        for (y in 0..listaGuardada.size-1) {
            if(listaGuardada[y].packageName == "carpeta"){
                val numCarpetaActual = (abs(listaGuardada[y].listaCarpeta!!))
                listaDeCarpetas.add(ArrayList())
                listaDeCarpetas[(listaDeCarpetas.size-1)].add(y)
                listaDeCarpetas[(listaDeCarpetas.size-1)].add(numCarpetaActual)
                listaAMostrar.set(y,
                    AppInfo(
                        ArrayList(),
                        listaGuardada[y].label,
                        "carpeta",
                        null,
                        Color.RED
                    )
                )
            }
        }
        //agregar las apps:
        for (x in 0..listaDelCelu.size-1) {
            var existeEnGuardados = false
            for (y in 0..listaGuardada.size-1) {
                if (listaGuardada[y].listaCarpeta == null){ //la guardada no es una carpeta
                    if(listaDelCelu[x].packageName == listaGuardada[y].packageName){
                        listaDelCelu[x].visible = listaGuardada[y].visible
                        listaAMostrar.set(y, listaDelCelu[x])
                        existeEnGuardados = true
                    }
                }
                /*
                else if (listaGuardada[y].packageName == "carpeta" && listaAMostrar[y] == null){ //la app guardada es una carpeta, esta no existe en las mostradas y la app del celu actual pertenece a ella
                        if (abs(listaGuardada[y+1].listaCarpeta!!) == abs(listaGuardada[y].listaCarpeta!!)){
                            carpetaAgregandoActual = y
                            Log.d("agregar a:", carpetaAgregandoActual.toString())
                            listaAMostrar.set(carpetaAgregandoActual, AppInfo(ArrayList(), listaGuardada[y].label, "carpeta", null, Color.RED))
                        }
                }
                */
                else if (listaGuardada[y].listaCarpeta != null && listaGuardada[y].listaCarpeta!! > 0 && listaGuardada[y].packageName != "carpeta") { //la guardada es de una carpeta
                            if(listaDelCelu[x].packageName == listaGuardada[y].packageName){
                                var indiceDeLaCarpeta = 0
                                for (i in 0..listaDeCarpetas.size-1){
                                    if (listaDeCarpetas[i][1] == listaGuardada[y].listaCarpeta){
                                        indiceDeLaCarpeta = listaDeCarpetas[i][0]
                                    }
                                }
                                listaAMostrar[indiceDeLaCarpeta]!!.listaCarpeta!!.add(listaDelCelu[x])
                                existeEnGuardados = true
                                /*
                                if (carpetaAgregandoActual != 0){
                                    Log.d("agregado a:", carpetaAgregandoActual.toString())
                                    listaAMostrar[carpetaAgregandoActual]!!.listaCarpeta!!.add(listaDelCelu[x])
                                    existeEnGuardados = true
                                } else{
                                    Log.d("agregaERROR: ", listaGuardada[y].label.toString())
                                    existeEnGuardados = true
                                }*/
                            }
                }
            }
            if (existeEnGuardados == false){
                cantidadDeAppsAAgregar++
                listaAMostrar.set( (listaGuardada.size-1+cantidadDeAppsAAgregar), listaDelCelu[x] )
            }
        }

        return listaAMostrar.filterNotNull()
    }

    fun listaMostrableAListaGuardable(listaIn: ArrayList<AppInfo>): ArrayList<AppGuardable>{ //las que son carpetas tienen el "numeroDeCarpeta" en negativo, las que pertenecen a una en positivo (empieza en 1)
        var listaNueva = ArrayList<AppGuardable>()
        var numeroDeCarpeta = 0
        for (i in 0..listaIn.size-1) {
            if (listaIn[i].listaCarpeta != null) {
                numeroDeCarpeta++
                val carpetaNueva =
                    AppGuardable(
                        -numeroDeCarpeta,
                        listaIn[i].label,
                        "carpeta",
                        listaIn[i].color
                    )
                listaNueva.add(carpetaNueva)
                val listaCarpeta = listaIn[i].listaCarpeta!!
                for (j in 0..listaCarpeta.size - 1) {
                    val appNueva =
                        AppGuardable(
                            numeroDeCarpeta,
                            listaCarpeta[j].label,
                            listaCarpeta[j].packageName,
                            listaCarpeta[j].color,
                            listaCarpeta[j].visible
                        )
                    listaNueva.add(appNueva)
                }
            }
            else{
                val appNueva: AppGuardable
                appNueva = AppGuardable(
                    null,
                    listaIn[i].label,
                    listaIn[i].packageName,
                    listaIn[i].color,
                    listaIn[i].visible
                )
                if(listaIn[i].visible == false){
                    Log.d("invisiblealguardarse: ", listaIn[i].label)
                }
                listaNueva.add(appNueva)
            }
        }
        return listaNueva
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
        /*if (Configs.cambiado == true){
            Configs.cambiado = false
            reload()
        }*/
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