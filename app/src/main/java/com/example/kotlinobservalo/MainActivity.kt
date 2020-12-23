
/*
Cosas que están mal y no sé cómo arreglar:
no funciona desinstalar app (el permiso no anda)
El recycler no pagina
Las apps no se mueven como deberían al cambiarlas de lugar
No funciona el intent de actualizaciones y demás
Se repite el ícono en las carpetas
*/

package com.example.kotlinobservalo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.example.kotlinobservalo.Paint.appHeight
import com.example.kotlinobservalo.Paint.appWidth
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil


class MainActivity : AppCompatActivity() {

    private val snapear = false

    private var listaSeparador:RecyclerView? = null
    private var layoutManagerSeparador:RecyclerView.LayoutManager? = null
    private var adaptadorSeparador:SeparadorAdapter? = null

    var lista:RecyclerView? = null
    private var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null

    lateinit var layout: FrameLayout

    lateinit var listaDeApps: ArrayList<AppInfo>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        IntentFilter("android.intent.action.PACKAGE_CHANGED")

        val filter0 = IntentFilter("android.intent.action.PACKAGE_ADDED")
        val receiver = PackageUpdatesReceiver()
        registerReceiver(receiver, filter0) //funciona con el modo avión

        Paint.alturaDeLaPantalla = getDisplayContentSize('h')
        Paint.anchuraDeLaPantalla = getDisplayContentSize('w')

        val tinydb = TinyDB(this)

        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //esto no funciona ¿razón?
/*
        val scalingFactor = 0.5f // scale down to half the size
        View.setScaleX(scalingFactor)
        View.setScaleY(scalingFactor)
*/

        layout = findViewById(R.id.LinearLayout)

        val separacion = 5
        var cantFilas: Int

        if (Configs.cantColumnas() == 0) Configs.cantColumnas(1)
        val cantColumnas = Configs.cantColumnas()

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
            if (Configs.obtenerBoolean("modoFondo")){
                val wm = WallpaperManager.getInstance(this)
                val d = wm.peekDrawable()
                layout.background = d // You can also use rl.setBackgroundDrawable(getWallpaper);
            }
            else {
                lista!!.setBackgroundColor(Paint.colorFondo())
            }

        listaDeApps = AppGetter.getListaDeApps(this.applicationContext)


        val configAct = AppInfo(
            null,
            "Configurar Launcher",
            "LclObservaloConfigActivity",
            ContextCompat.getDrawable(this, R.drawable.ic_config),
            Paint.colorObservaloApp("Config")
            )
        listaDeApps.add(configAct)

        val llamadasAct = AppInfo(
            null,
            "Llamadas",
            "LclObservaloLlamadasActivity",
            ContextCompat.getDrawable(this, R.drawable.ic_llamadas_emergencia),
            Paint.colorObservaloApp("Llamadas")
        )
        listaDeApps.add(llamadasAct)

        val lupaAct = AppInfo(
            null,
            "Lupa",
            "LclObservaloLupa",
            ContextCompat.getDrawable(this, R.drawable.ic_lupa),
            color = Paint.colorObservaloApp("Lupa")
        )
        listaDeApps.add(lupaAct)

        val listaDeAppsGuardadas = tinydb.getListaGuardada("list3")

        listaDeApps = listaGuardadaAListaAMostrarActualizando(listaDeAppsGuardadas, listaDeApps).toCollection(ArrayList())

        val listaDeInvisibles = getListaDeInvisibles(listaDeApps)
        listaDeApps = purgeInvisibles(listaDeApps)

        layoutManager = GridLayoutManager(this, cantFilas, RecyclerView.HORIZONTAL, false)
        layoutManager!!.canScrollHorizontally()

        val itemDeco = ItemOffsetDecoration(separacion)
        lista!!.addItemDecoration(itemDeco)

        fun onConfigHappenedInAppAdapter(evento: String, packageName: String){
            when {
                evento == "appEscondida" -> {
                    ocultarUnaAppYNotificarAlRecycler(null, packageName)
                }
                evento == "appDesinstalada" -> {
                    desinstalarUnaAppYNotificarAlRecycler(null, packageName)
                }
                evento == "appSacadaDeCarpeta" -> {
                    sacarUnaAppDeCarpetaYNotificarAlRecycler(packageName)
                }
                evento.contains("tituloCarpetaEditada") -> {
                    val position = evento.filter { it.isDigit() }
                    cambiarNombreCarpeta(position.toInt(), packageName)
                }
            }
        }

        adaptador = AppAdapter(listaDeApps){ evento, pos -> onConfigHappenedInAppAdapter(evento, pos) }

        //acá van las cosas sobre el modo de configuración del órden y demás:
        val fab: View = findViewById(R.id.fab)
        val borrarOCarpeta: View = findViewById(R.id.borrarOCarpeta)
        if (Configs.modoConfig){
            if (listaDeInvisibles.size > 0){
                listaDeApps.addAll(listaDeInvisibles)
            }

            fab.visibility = VISIBLE
            fab.setOnClickListener {
                tinydb.putListaGuardada("list3", listaMostrableAListaGuardable(listaDeApps))
                terminarModoConfig()
            }

            borrarOCarpeta.visibility = INVISIBLE
            val duracionAnimacionBotones = 500.toLong()
            val borrarOCarpeta_borrar = borrarOCarpeta.findViewById<Button>(R.id.borrar)
            val borrarOCarpeta_carpeta = borrarOCarpeta.findViewById<Button>(R.id.carpeta)
            var x: Float
            var y: Float
            var enBotonBorrar = false
            var enBotonCarpeta = false
            var appAgarrada: Int? = null
            var carpetaDestino: Int? = null

            lista!!.setOnTouchListener { v, event ->
                if (borrarOCarpeta.visibility == VISIBLE) {
                    x = event.x
                    y = event.y
                    enBotonBorrar =
                        y < borrarOCarpeta_borrar.bottom && x < borrarOCarpeta_borrar.right && x > borrarOCarpeta_borrar.left
                    enBotonCarpeta =
                        y < borrarOCarpeta_carpeta.bottom && x < borrarOCarpeta_carpeta.right && x > borrarOCarpeta_carpeta.left
                }
                false
            }

            val itemTouchHelperCallback =
                object :
                    ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP  or ItemTouchHelper.DOWN, 0) {
                    override fun onMove( recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder ): Boolean {
                        val fromPos: Int = viewHolder.adapterPosition
                        val toPos: Int = target.adapterPosition

                        appAgarrada = null

                        if(listaDeApps[toPos].packageName == "carpeta" && listaDeApps[fromPos].packageName != "carpeta"){    //si lo tira a una carpeta
                            if(appAgarrada == null){
                                appAgarrada = fromPos
                            }
                            carpetaDestino = toPos
                        }
                        else {                                            //si lo tira a una posición común:
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
                                    borrarOCarpeta.alpha = 0f
                                    borrarOCarpeta.visibility = VISIBLE
                                    borrarOCarpeta.animate()
                                        .alpha(1f)
                                        .setDuration(duracionAnimacionBotones)
                                        .setListener(null)
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
                                                borrarOCarpeta.visibility = GONE
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
                                                borrarOCarpeta.visibility = GONE
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
            fab.visibility = GONE
            borrarOCarpeta.visibility = GONE
        }

        lista!!.layoutManager = layoutManager
        lista!!.adapter = adaptador


        if (snapear) {
            listaSeparador = findViewById(R.id.listaSeparador)

            layoutManagerSeparador = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(listaSeparador)

            var cantidadDeSeparadores: Float = listaDeApps.size.toFloat()
            cantidadDeSeparadores = ceil(cantidadDeSeparadores/cantFilas/cantColumnas)
            adaptadorSeparador = SeparadorAdapter(cantidadDeSeparadores.toInt(), getDisplayContentSize('w'), getDisplayContentSize('h'))

            //clicks:
            listaSeparador!!.addOnItemTouchListener(PasadorDeClicks(lista!!))

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

    class PasadorDeClicks(private var recyclerAClickar: RecyclerView) : OnItemTouchListener {
        override fun onInterceptTouchEvent( rv: RecyclerView, e: MotionEvent ): Boolean {
            //opciones:
            if (e.action != MotionEvent.ACTION_MOVE && e.action != MotionEvent.ACTION_UP) { //apreta el botón debajo del principio del scroll
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

    /*
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
    */

    class PackageUpdatesReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("a", "se instaloooooooooooooooooooooooooooooo")
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
            for (i in 0 until listaDeApps.size) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0 until listaCarpeta!!.size) {
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
    private fun desinstalarUnaAppYNotificarAlRecycler(posApp: Int? = null, packageName: String){
        var nombreDelPaquete = packageName
        if (posApp != null){ //si se quiere hacer mediante la posición en el recycler
            listaDeApps[posApp].visible = false
            listaDeApps.add(listaDeApps[posApp])
            listaDeApps.removeAt(posApp)
            nombreDelPaquete = listaDeApps[posApp].packageName
        }
        else{ //si se quiere hacer mediante el nombre )
            for (i in 0 until listaDeApps.size) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0 until listaCarpeta!!.size) {
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
        Log.d("observame","app por desinstalarse")
        AppGetter.uninstall(nombreDelPaquete)
    }
    private fun cambiarNombreCarpeta(position: Int, label: String){
        listaDeApps[position].label = label
        adaptador!!.notifyDataSetChanged()
    }


    private fun sacarUnaAppDeCarpetaYNotificarAlRecycler(packageName: String? = null){
            for (i in 0 until listaDeApps.size) {
                if (listaDeApps[i].listaCarpeta != null) {
                    val listaCarpeta = listaDeApps[i].listaCarpeta
                    for (j in 0 until listaCarpeta!!.size) {
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

    private fun getListaDeInvisibles(listaIn: ArrayList<AppInfo>): ArrayList<AppInfo>{
        val listaOut = ArrayList<AppInfo>()
        for (i in 0 until listaIn.size){
            if(!listaIn[i].visible){
                listaOut.add(listaIn[i])
            }
        }
        return listaOut
    }

    private fun purgeInvisibles(listaIn: ArrayList<AppInfo>): ArrayList<AppInfo>{
        for (i in listaIn.size-1 downTo 0){ //TODO ese 2 me da miedo
            if(!listaIn[i].visible){
                listaIn.removeAt(i)
            }
        }
        return listaIn
    }

    private fun listaGuardadaAListaAMostrarActualizando(listaGuardada: ArrayList<AppGuardable>, listaDelCelu: ArrayList<AppInfo>): List<AppInfo> {
        val listaAMostrar = arrayOfNulls<AppInfo>(listaDelCelu.size + 5) //TODO OH GOD, ESTO ESTÁ TOTALMENTE MAL Y ES TERRIBLEMENTE INEFICIENTE
        var cantidadDeAppsAAgregar = 0

        val listaDeCarpetas = ArrayList<ArrayList<Int>>()
        //encontrar las carpetas:
        for (y in 0 until listaGuardada.size) {
            if(listaGuardada[y].packageName == "carpeta"){
                val numCarpetaActual = (kotlin.math.abs(listaGuardada[y].listaCarpeta!!))
                listaDeCarpetas.add(ArrayList())
                listaDeCarpetas[(listaDeCarpetas.size-1)].add(y)
                listaDeCarpetas[(listaDeCarpetas.size-1)].add(numCarpetaActual)
                listaAMostrar[y] = AppInfo(
                    ArrayList(),
                    listaGuardada[y].label,
                    "carpeta",
                    null,
                    Paint.colorCarpeta()
                )
            }
        }
        //agregar las apps:
        for (x in 0 until listaDelCelu.size) {
            var existeEnGuardados = false
            for (y in 0 until listaGuardada.size) {
                if (listaGuardada[y].listaCarpeta == null){ //la guardada no es una carpeta
                    if(listaDelCelu[x].packageName == listaGuardada[y].packageName){
                        listaDelCelu[x].visible = listaGuardada[y].visible
                        listaAMostrar[y] = listaDelCelu[x]
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
                                for (i in 0 until listaDeCarpetas.size){
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
            if (!existeEnGuardados){
                cantidadDeAppsAAgregar++
                listaAMostrar[(listaGuardada.size-1+cantidadDeAppsAAgregar)] = listaDelCelu[x]
            }
        }

        return listaAMostrar.filterNotNull()
    }

    private fun listaMostrableAListaGuardable(listaIn: ArrayList<AppInfo>): ArrayList<AppGuardable>{ //las que son carpetas tienen el "numeroDeCarpeta" en negativo, las que pertenecen a una en positivo (empieza en 1)
        val listaNueva = ArrayList<AppGuardable>()
        var numeroDeCarpeta = 0
        for (i in 0 until listaIn.size) {
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
                for (j in 0 until listaCarpeta.size) {
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
                val appNueva = AppGuardable(
                    null,
                    listaIn[i].label,
                    listaIn[i].packageName,
                    listaIn[i].color,
                    listaIn[i].visible
                )
                if(!listaIn[i].visible){
                    Log.d("invisiblealguardarse: ", listaIn[i].label)
                }
                listaNueva.add(appNueva)
            }
        }
        return listaNueva
    }

    private fun terminarModoConfig(){
        Configs.modoConfig = false
        AppGetter.launch("LclObservaloConfigActivity")
    }

    override fun onBackPressed() {
        if(Configs.modoConfig){
            terminarModoConfig()
        }
    }

    //Busca la altura de la pantalla
    private fun getDisplayContentSize(wh: Char): Int {
        val windowManager: WindowManager = windowManager
        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        return if (wh == 'w') {
            //Saca el ancho de la pantalla
            size.x
        } else{
            var actionBarHeight = 0
            var statusBarHeight = 0
            if (actionBar != null) {
                actionBarHeight = actionBar!!.height
            }

            //Saca parámetros del dispositivo
            val resourceId: Int =
                resources.getIdentifier("status_bar_height", "dimensions", "android")

            //Si resourceId existe lo toma y calcula
            //la cantidad de píxeles de la pantalla
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }


            //Encuentra el borde superior
            //val contentTop: Int = Contexto.app.findViewById(android.R.id.content).getTop()

            //Saca el alto de la pantalla
            val screenHeight: Int = size.y
            screenHeight - actionBarHeight - statusBarHeight //- contentTop
        }
    }

    class ItemOffsetDecoration(private val mItemOffset: Int) : ItemDecoration() {
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