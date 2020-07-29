package com.example.kotlinobservalo

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.Color.argb
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat.setBackground
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.Config.Configs
import kotlinx.android.synthetic.main.app_equisemel.view.*

var contador = 0

class AppAdapter(items: MutableList<AppInfo>?, cellWidth: Int, cellHeight: Int): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:MutableList<AppInfo>? = null

    var viewHolder:ViewHolder? = null

    var packageName: String = ""

    var cellWidth: Int = 200
    var cellHeight: Int = 200

    init{
        this.items = items
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
        this.cellHeight = cellHeight
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items?.get(position)
        packageName = item!!.packageName

        if (Configs.modoConfig != true){
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

        if (Configs.obtenerBoolean("modoAltoContraste") == true){
            if (Configs.obtenerBoolean("modoNoche") == true){
                holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_highContrast_dark, null))
            }
            else{
                holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_highContrast_light, null))
            }
        }
        else{
            if (Configs.obtenerBoolean("modoFondo") == false){
                if (Configs.obtenerBoolean("modoNoche") == true){
                    holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_dark, null))
                }
                else{
                    holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_light, null))
                }
            } else {
                if (Configs.obtenerBoolean("modoNoche") == true) {
                    holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_fondo_dark,null))
                } else {
                    holder.label!!.setShadowLayer(4.0f,0.0f, 0.0f, argb(200, 0, 0, 0))
                    holder.label!!.setTextColor(ResourcesCompat.getColor(Contexto.mainActivity.getResources(), R.color.text_fondo_light,null))
                }
            }
        }

        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
        holder.layout!!.setLayoutParams(lp)

    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.app_equisemel, parent, false)
        viewHolder = ViewHolder(v)
        return viewHolder!!
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var v = v
        var icon:ImageView? = null
        var label:TextView? = null
        var layout: LinearLayout? = null

        init {
            icon = v.icon
            label = v.label
            layout = v.laiaut
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