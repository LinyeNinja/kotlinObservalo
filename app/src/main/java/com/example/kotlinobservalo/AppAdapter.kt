package com.example.kotlinobservalo

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.Window
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


var contador = 0

class AppAdapter(items: MutableList<AppInfo>?, val onConfigHappened : (String, String) -> Unit): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:MutableList<AppInfo>? = null

    var viewHolder: ViewHolder? = null

    var packageName: String = ""

    var cellWidth: Int = 200
    var cellHeight: Int = 200

    var esCarpeta = false

    lateinit var view: View

    lateinit var patro: ViewGroup //el viewgroup/recyclerview al que pertenecen los cosos

    lateinit var adaptadorCarpeta: AppAdapter

    init{
        this.items = items
        this.cellWidth = Paint.appWidth
        this.cellHeight = Paint.appHeight
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
            else{
                if (patro.height < Paint.alturaDeLaPantalla){
                    holder.v.setOnLongClickListener(
                        object: View.OnLongClickListener {
                            override fun onLongClick(v: View?): Boolean {
                                val listaBtns = ArrayList<Btns>()
                                listaBtns.add(Btns("sacar de carpeta", Color.rgb(0xFF, 0xFF, 0x00)))
                                listaBtns.add(Btns("ocultar", Color.rgb(0xFF, 0xA5, 0x00)))
                                listaBtns.add(Btns("desinstalar", Color.rgb(0xFF, 0x00, 0x00)))
                                fun onOptionButtonClick(btn: Int){
                                    if (btn == 0){ //sacar de carpeta
                                        onConfigHappened("appSacadaDeCarpeta", packageName)
                                    }
                                    else if (btn == 1){ //oscultar
                                        onConfigHappened("appEscondida", packageName)
                                    }
                                    else if (btn == 2){ //desinstalar
                                        onConfigHappened("appDesinstalada", packageName)
                                    }
                                }

                                val dialogFragment: DialogOpciones = DialogOpciones(listaBtns) { position -> onOptionButtonClick(position) }
                                val manager = (Contexto.mainActivity as FragmentActivity).supportFragmentManager //TODO ese contexto es peligrosoooo, antes decía holder.algoblabla
                                dialogFragment.show(manager, "MyFragment")

                                return true
                            }
                        }
                    )
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
            }
            */

            var gradientDrawable:GradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = radio

            if (item.visible != false) {
                gradientDrawable.setColor(item.color)
            }
            else{
                gradientDrawable.setColor(Color.rgb(0x00, 0x00, 0x00))
            }
            holder.layout!!.setBackground(gradientDrawable)

            holder.icon!!.getLayoutParams().height = (cellHeight * 0.5).toInt()
            holder.icon!!.getLayoutParams().width = holder.icon!!.getLayoutParams().height
            holder.label!!.textSize = holder.icon!!.getLayoutParams().width * 0.115f

            holder.label!!.setTextColor(Paint.colorAppLabel())


            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.setLayoutParams(lp)

        }
        else { //si es una carpeta
            holder.label!!.text = item!!.label

            val listaCarpeta = item.listaCarpeta!!

            val cantAppsEnCarpeta = listaCarpeta.size

            holder.casaDeIconos.getLayoutParams().height = ((cellHeight * 0.5).toInt())
            holder.casaDeIconos.getLayoutParams().width = ((cellHeight * 0.5).toInt())
            var tamIconoEnCarpeta = holder.casaDeIconos.getLayoutParams().width/2


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


            holder.icon!!.getLayoutParams().height = tamIconoEnCarpeta
            holder.icon!!.getLayoutParams().width = tamIconoEnCarpeta
            holder.icon2!!.getLayoutParams().height = tamIconoEnCarpeta
            holder.icon2!!.getLayoutParams().width = tamIconoEnCarpeta
            holder.icon3!!.getLayoutParams().height = tamIconoEnCarpeta
            holder.icon3!!.getLayoutParams().width = tamIconoEnCarpeta
            holder.icon4!!.getLayoutParams().height = tamIconoEnCarpeta
            holder.icon4!!.getLayoutParams().width = tamIconoEnCarpeta

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

            holder.label!!.textSize = ((cellHeight * 0.5).toInt()) * 0.115f

            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
            holder.layout!!.setLayoutParams(lp)

                holder.v.setOnClickListener(
                    object: View.OnClickListener {
                        override fun onClick(view: View) {
                            val DialogCarpeta = DialogCarpeta(item.label, position, listaCarpeta, onConfigHappened)
                            val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager
                            DialogCarpeta.show(manager, "MyFragment")
                        }
                    }
                )
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