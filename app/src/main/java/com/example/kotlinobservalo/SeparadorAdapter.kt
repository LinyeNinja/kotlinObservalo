package com.example.kotlinobservalo

import android.content.Intent
import android.graphics.Color
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

class SeparadorAdapter(cantidad: Int, cellWidth: Int, cellHeight: Int): RecyclerView.Adapter<SeparadorAdapter.ViewHolder>(){

    var cantidad: Int = 0

    var viewHolder:ViewHolder? = null

    var cellWidth: Int = 500
    var cellHeight: Int = 200

    init{
        this.cantidad = cantidad
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (Configs.modoConfig != true){

        }

        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
        holder.layout!!.setLayoutParams(lp)

    }

    override fun getItemCount(): Int {
        return this.cantidad
        //return this.items?.count()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeparadorAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.separador_equisemel, parent, false)
        viewHolder = ViewHolder(v)
        return viewHolder!!
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var v = v
        var layout: LinearLayout? = null

        init {
            layout = v.laiaut
        }
    }

}