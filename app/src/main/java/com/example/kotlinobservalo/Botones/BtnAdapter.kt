package com.example.kotlinobservalo.Botones

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.R
import kotlinx.android.synthetic.main.boton_solotexto.view.*

class btnAdapter(items: MutableList<Btns>?, val onItemClick : (Int) -> Unit): RecyclerView.Adapter<btnAdapter.ViewHolder>() {

    var items: MutableList<Btns>? = null

    var viewHolder: ViewHolder? = null

    lateinit var view: View

    init {
        this.items = items
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items?.get(position)

        holder.btn!!.setOnClickListener{
            onItemClick(position)
        }

        holder.btn!!.text = item!!.label

        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 20f
        gradientDrawable.setColor(item.color)

        holder.btn!!.background = gradientDrawable
    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        items?.get(position)
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(parent.context).inflate(R.layout.boton_solotexto, parent, false)
        return ViewHolder(
            view
        )
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var v = v

        var btn: TextView? = null
        var layout: LinearLayout? = null

        init {
            btn = v.btn
            layout = v.laiaut
        }
    }
}