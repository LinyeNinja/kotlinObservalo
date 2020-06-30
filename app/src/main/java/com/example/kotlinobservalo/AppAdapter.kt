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
import androidx.core.view.ViewCompat.setBackground
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_equisemel.view.*

class AppAdapter(items: ArrayList<AppInfo>?, cellWidth: Int, cellHeight: Int): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:ArrayList<AppInfo>? = null

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

        holder.v.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(view: View) {
                    AppGetter.launch(item.packageName)
                    Log.d("a", item.packageName)
                }
            }
        )

        holder.label?.text = item.label

        if (item.icon != null) {
            holder.icon?.setImageDrawable(item.icon!!)
        }

        var gradientDrawable:GradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = 20f

        gradientDrawable.setColor(item.color)

        holder.layout!!.setBackground(gradientDrawable)

        holder.icon!!.getLayoutParams().height = (cellHeight * 0.5).toInt()
        holder.icon!!.getLayoutParams().width = holder.icon!!.getLayoutParams().height
        holder.label!!.textSize = holder.icon!!.getLayoutParams().width * 0.115f

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



}