package com.example.kotlinobservalo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.Config.Configs
import kotlinx.android.synthetic.main.app_equisemel.view.*

class SeparadorAdapter(cantidad: Int, cellWidth: Int, cellHeight: Int): RecyclerView.Adapter<SeparadorAdapter.ViewHolder>(){

    private var cantidad: Int = 0

    private var viewHolder:ViewHolder? = null

    private var cellWidth: Int = 500
    private var cellHeight: Int = 200

    init{
        this.cantidad = cantidad
        this.cellWidth = cellWidth
        this.cellHeight = cellHeight
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (!Configs.modoConfig){

        }

        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)
        holder.layout!!.layoutParams = lp

    }

    override fun getItemCount(): Int {
        return this.cantidad
        //return this.items?.count()!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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