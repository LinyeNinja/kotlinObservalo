package com.example.kotlinobservalo

import android.content.Context
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.Config.Configs
import kotlinx.android.synthetic.main.app_equisemel.view.*
import kotlinx.android.synthetic.main.app_equisemel.view.icon
import kotlinx.android.synthetic.main.app_equisemel.view.label
import kotlinx.android.synthetic.main.app_equisemel.view.laiaut
import kotlinx.android.synthetic.main.carpeta_equisemel.view.*

var contador = 0

class AppAdapter(items: MutableList<AppInfo>?, cellWidth: Int, cellHeight: Int): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:MutableList<AppInfo>? = null

    var viewHolder:ViewHolder? = null

    var packageName: String = ""

    var cellWidth: Int = 200
    var cellHeight: Int = 200

    var esCarpeta = false

    lateinit var view: View

    init{
        this.items = items
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items?.get(position)

        if (!esCarpeta){
            packageName = item!!.packageName

            if (Configs.modoConfig == false){
                holder.v.setOnClickListener(
                    object: View.OnClickListener {
                        override fun onClick(view: View) {
                            AppGetter.launch(item.packageName)
                        }
                    }
                )
            }

            holder.label?.text = item.label

            if (item.icon != null) {
                holder.icon?.setImageDrawable(item.icon!!)
            }
            /* //ESTO ES PARA OBTENER EL ÍCONO DIRECTAMENTE DEL TELÉFONO EN VEZ DE CARGARLO DE APPINFO
            if (packageName != "LclObservaloConfigActivity") {
                val icon = Contexto.app.getPackageManager().getApplicationIcon(item.packageName)
                if (icon != null) {
                    holder.icon?.setImageDrawable(icon)
                }
            }
            */

            var gradientDrawable:GradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 20f

            gradientDrawable.setColor(item.color)

            holder.layout!!.setBackground(gradientDrawable)

            holder.icon!!.getLayoutParams().height = (cellHeight * 0.5).toInt()
            holder.icon!!.getLayoutParams().width = holder.icon!!.getLayoutParams().height
            holder.label!!.textSize = holder.icon!!.getLayoutParams().width * 0.115f

            holder.label!!.setTextColor(Paint.colorAppLabel())


            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.setLayoutParams(lp)
        }
        else{ //si es una carpeta

            holder.label!!.text = item!!.label

            val listaCarpeta = item.listaCarpeta!!

            val cantAppsEnCarpeta = listaCarpeta.size

            if (cantAppsEnCarpeta > 0){
                holder.icon!!.setImageDrawable(listaCarpeta[0].icon!!)
                if (cantAppsEnCarpeta > 1){
                    holder.icon2!!.setImageDrawable(listaCarpeta[1].icon!!)
                    if (cantAppsEnCarpeta > 2){
                        holder.icon3!!.setImageDrawable(listaCarpeta[2].icon!!)
                        if (cantAppsEnCarpeta > 3){
                            holder.icon4!!.setImageDrawable(listaCarpeta[3].icon!!)
                        }
                        else{
                            holder.icon2!!.visibility = GONE
                        }
                    }
                    else{
                        holder.icon2!!.visibility = GONE
                    }
                }
                else{
                    holder.icon2!!.visibility = GONE
                }
            }
            else{
                holder.icon2!!.visibility = GONE
            }

            for ( i in 0..cantAppsEnCarpeta-1 ){
                val app = listaCarpeta[i]
                if (app.icon != null){
                    holder.icon!!.setImageDrawable(app.icon!!)
                }
                if (i == 3){
                    break
                }
            }

            var gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 20f

            gradientDrawable.setColor(item.color)

            holder.layout!!.setBackground(gradientDrawable)

            holder.icon!!.getLayoutParams().height = (cellHeight * 0.5).toInt()
            holder.icon!!.getLayoutParams().width = holder.icon!!.getLayoutParams().height
            holder.label!!.textSize = holder.icon!!.getLayoutParams().width * 0.115f

            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.setLayoutParams(lp)

            if (Configs.modoConfig == false){ //dialog example
                holder.v.setOnClickListener(
                    object: View.OnClickListener {
                        override fun onClick(view: View) {
                            val popupInflater:LayoutInflater = Contexto.mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val popupView = popupInflater.inflate(R.layout.carpeta_abierta,null)

                            // Initialize a new instance of popup window
                            val popupWindow = PopupWindow(
                                popupView, // Custom view to show in popup window
                                LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
                                LinearLayout.LayoutParams.MATCH_PARENT // Window height
                            )

                            /*// Set an elevation for the popup window
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                popupWindow.elevation = 10.0F
                            }*/


                            // If API level 23 or higher then execute the code
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                val slideIn = Fade() //TODO estas animaciones son feas, debería hacer un ¡pop!
                                popupWindow.enterTransition = slideIn
                                val slideOut = Fade()
                                popupWindow.exitTransition = slideOut

                            }

                            // Get the widgets reference from custom view
                            val carpetaRecycler: RecyclerView = popupView.findViewById(R.id.listaCarpeta)
                            val params: ViewGroup.LayoutParams = carpetaRecycler.getLayoutParams()
                            val separacion = 5
                            val cantColumnas = Configs.cantColumnas()
                            params.height = cantColumnas*cellHeight+separacion*cantColumnas*2

                            val layoutManagerCarpeta = GridLayoutManager(Contexto.mainActivity, cantColumnas, RecyclerView.VERTICAL, false)

                            val itemDecoCarpeta = MainActivity.ItemOffsetDecoration(separacion)
                            carpetaRecycler.addItemDecoration(itemDecoCarpeta)

                            val adaptadorCarpeta = AppAdapter(listaCarpeta, cellWidth, cellHeight)

                            carpetaRecycler.layoutManager = layoutManagerCarpeta
                            carpetaRecycler.adapter = adaptadorCarpeta

                            val fondo: ConstraintLayout = popupView.findViewById(R.id.fondo)
                            fondo.setOnClickListener{
                                popupWindow.dismiss() //para cerrar
                            }

                            popupWindow.setOnDismissListener {
                                //esto se ejecuta cuando se cierra el popup
                            }

                            // Finally, show the popup window on app
                            TransitionManager.beginDelayedTransition(holder.layout!!)
                            popupWindow.showAtLocation(
                                holder.layout!!, // Location to display popup window (acá lo mismo que arriba, deben ser iguales (?))
                                Gravity.CENTER, // Exact position of layout to display popup
                                0, // X offset
                                0 // Y offset
                            )
                        }
                    }
                )
            }



            /*

            val carpetaPopup = findViewById(R.id.listaCarpeta)
            val params: ViewGroup.LayoutParams = carpetaPopup!!.getLayoutParams()
            params.height = cantColumnas*appHeight+separacion*cantColumnas*2

            val layoutManagerCarpeta = GridLayoutManager(this, cantColumnas, RecyclerView.VERTICAL, false)

            var itemDecoCarpeta = MainActivity.ItemOffsetDecoration(separacion)
            carpetaPopup?.addItemDecoration(itemDecoCarpeta)

            adaptadorCarpeta = AppAdapter(listaCarpeta, appWidth, appHeight)

            carpetaPopup!!.layoutManager = layoutManagerCarpeta
            carpetaPopup!!.adapter = adaptadorCarpeta

            listaCarpeta!!.visibility = GONE
            */

        }

    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        val item = items?.get(position)
        if (item?.listaCarpeta != null){
            esCarpeta = true
            return 1
        }
        else{
            esCarpeta = false
            return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 0) { //no es una carpeta
            view = LayoutInflater.from(parent.context).inflate(R.layout.app_equisemel, parent, false)
        }
        else{ //es una carpeta
            view = LayoutInflater.from(parent.context).inflate(R.layout.carpeta_equisemel, parent, false)
            //return ViewHolderCarpeta(v)
        }
        return ViewHolder(view, viewType)
    }

    class ViewHolder(v: View, viewType: Int): RecyclerView.ViewHolder(v){
        var v = v

        var icon:ImageView? = null
        var icon2:ImageView? = null
        var icon3:ImageView? = null
        var icon4:ImageView? = null
        var label:TextView? = null
        var layout: LinearLayout? = null

        init {
            if (viewType == 0){ //es app
                icon = v.icon
                label = v.label
                layout = v.laiaut
            }
            else{ //es carpeta
                icon = v.icon
                icon2 = v.icon2
                icon3 = v.icon3
                icon4 = v.icon4
                label = v.label
                layout = v.laiaut
            }
        }
    }

    //para el drag and drop
    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                items!!.set(i, items!!.set(i+1, items!!.get(i)));
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                items!!.set(i, items!!.set(i-1, items!!.get(i)));
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }
}