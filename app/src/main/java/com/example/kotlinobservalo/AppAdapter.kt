package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.gridlayout.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.Botones.Btns
import com.example.kotlinobservalo.ClasesDeInfo.AppInfo
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Paint.radio
import com.example.kotlinobservalo.Popups.DialogCarpeta
import com.example.kotlinobservalo.Popups.DialogOpciones
import kotlinx.android.synthetic.main.app_equisemel.view.icon
import kotlinx.android.synthetic.main.app_equisemel.view.label
import kotlinx.android.synthetic.main.app_equisemel.view.laiaut
import kotlinx.android.synthetic.main.carpeta_equisemel.view.*
import java.util.*

class AppAdapter(items: MutableList<AppInfo>?, val onConfigHappened : (String, String) -> Unit): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:MutableList<AppInfo>? = null

    private var packageName: String = ""

    private var cellWidth: Int = 200
    private var cellHeight: Int = 200

    private var esCarpeta = false

    lateinit var view: View

    private lateinit var patro: ViewGroup //el viewgroup/recyclerview al que pertenecen los cosos

    private lateinit var adaptadorCarpeta: AppAdapter

    init{
        this.items = items
        this.cellWidth = Paint.appWidth
        this.cellHeight = Paint.appHeight
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)

        if (!esCarpeta){
            packageName = item!!.packageName

            if (!Configs.modoConfig){
                holder.v.setOnClickListener { AppGetter.launch(item.packageName) }
            }

            else{
                if (patro.height < Paint.alturaDeLaPantalla){
                    holder.v.setOnLongClickListener {
                        val listaBtns = ArrayList<Btns>()
                        listaBtns.add(Btns("sacar de carpeta", Color.rgb(0xFF, 0xFF, 0x00)))
                        listaBtns.add(Btns("ocultar", Color.rgb(0xFF, 0xA5, 0x00)))
                        listaBtns.add(Btns("desinstalar", Color.rgb(0xFF, 0x00, 0x00)))
                        fun onOptionButtonClick(btn: Int) {
                            when (btn) {
                                0 -> { //sacar de carpeta
                                    onConfigHappened("appSacadaDeCarpeta", packageName)
                                }
                                1 -> { //oscultar
                                    onConfigHappened("appEscondida", packageName)
                                }
                                2 -> { //desinstalar
                                    onConfigHappened("appDesinstalada", packageName)
                                }
                            }
                        }

                        val dialogFragment =
                            DialogOpciones(listaBtns) { position -> onOptionButtonClick(position) }
                        val manager =
                            (Contexto.mainActivity as FragmentActivity).supportFragmentManager //TODO ese contexto es peligrosoooo, antes decía holder.algoblabla
                        dialogFragment.show(manager, "MyFragment")

                        true
                    }
                }
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
            } */

            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = radio

            if (item.visible) {
                gradientDrawable.setColor(item.color)
            }

            else {
                gradientDrawable.setColor(Color.rgb(0x00, 0x00, 0x00))
            }

            holder.layout!!.background = gradientDrawable

            holder.icon!!.layoutParams.height = (cellHeight * 0.5).toInt()
            holder.icon!!.layoutParams.width = holder.icon!!.layoutParams.height
            holder.label!!.textSize = holder.icon!!.layoutParams.width * 0.115f

            holder.label!!.setTextColor(Paint.colorAppLabel())

            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.layoutParams = lp

        }

        else { //si es una carpeta

            holder.label!!.text = item!!.label

            val listaCarpeta = item.listaCarpeta!!

            val cantAppsEnCarpeta = listaCarpeta.size

            holder.casaDeIconos.layoutParams.height = ((cellHeight * 0.5).toInt())
            holder.casaDeIconos.layoutParams.width = ((cellHeight * 0.5).toInt())
            val tamIconoEnCarpeta = holder.casaDeIconos.layoutParams.width/2

            try{
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
                            holder.icon4!!.visibility = GONE
                        }
                    }
                    else{
                        holder.icon3!!.visibility = GONE
                        holder.icon4!!.visibility = GONE
                    }
                }
                else{
                    holder.icon2!!.visibility = GONE
                    holder.icon3!!.visibility = GONE
                    holder.icon4!!.visibility = GONE
                }
            }
            else{
                holder.icon!!.visibility = GONE
                holder.icon2!!.visibility = GONE
                holder.icon3!!.visibility = GONE
                holder.icon4!!.visibility = GONE
            }
            }catch(e: Exception){}

            holder.icon!!.layoutParams.height = tamIconoEnCarpeta
            holder.icon!!.layoutParams.width = tamIconoEnCarpeta
            holder.icon2!!.layoutParams.height = tamIconoEnCarpeta
            holder.icon2!!.layoutParams.width = tamIconoEnCarpeta
            holder.icon3!!.layoutParams.height = tamIconoEnCarpeta
            holder.icon3!!.layoutParams.width = tamIconoEnCarpeta
            holder.icon4!!.layoutParams.height = tamIconoEnCarpeta
            holder.icon4!!.layoutParams.width = tamIconoEnCarpeta

            for ( i in 0 until cantAppsEnCarpeta){
                val app = listaCarpeta[i]
                if (app.icon != null){
                    holder.icon!!.setImageDrawable(app.icon!!)
                }
                if (i == 3){
                    break
                }
            }

            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = 20f

            gradientDrawable.setColor(item.color)

            holder.layout!!.background = gradientDrawable

            holder.label!!.textSize = ((cellHeight * 0.5).toInt()) * 0.115f

            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.layoutParams = lp

                holder.v.setOnClickListener {
                    val DialogCarpeta =
                        DialogCarpeta(item.label, position, listaCarpeta, onConfigHappened)
                    val manager =
                        (holder.itemView.context as FragmentActivity).supportFragmentManager
                    DialogCarpeta.show(manager, "MyFragment")
                }
        }

    }

    fun carpetaDataSetChanged(){
        adaptadorCarpeta.notifyDataSetChanged()
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
        patro = parent

        view = if (viewType == 0) { //no es una carpeta
            LayoutInflater.from(parent.context).inflate(R.layout.app_equisemel, parent, false)
        } else{ //es una carpeta
            LayoutInflater.from(parent.context).inflate(R.layout.carpeta_equisemel, parent, false)
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
        var GridDeIconos: GridLayout? = null
        lateinit var casaDeIconos: ConstraintLayout

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
                casaDeIconos = v.CasaDeIconos
            }
        }
    }

    //para el drag and drop
    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items!!, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items!!, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

}