package com.example.kotlinobservalo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_equisemel.view.*

class AppAdapter(items: ArrayList<AppInfo>): RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    var items:ArrayList<AppInfo>? = null

    var viewHolder:ViewHolder? = null

    var item:AppInfo? = null

    var backgroundColor = 0

    var packageName: String = ""

    init{
        this.items = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        //backgroundColor = item!!.color
        packageName = item!!.packageName
        holder.label?.text = item?.label
        holder.icon?.setImageDrawable(item?.icon!!)
    }
    override fun getItemCount(): Int {
        return this.items?.count()!!
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.app_equisemel, parent, false)
        viewHolder = ViewHolder(v)

        //val layout: LinearLayout = v.findViewById(R.id.laiaut)
        //layout.setBackgroundColor(backgroundColor)

        //Para abrir apps
        v.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(view: View) {
                    val context = Contexto.app
                    val launchAppIntent: Intent? = context.getPackageManager()
                        .getLaunchIntentForPackage(packageName)
                    if (launchAppIntent != null) context.startActivity(launchAppIntent)
                }
            }
        )
        return viewHolder!!
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        var v = v
        var icon:ImageView? = null
        var label:TextView? = null

        init {
            icon = v.icon
            label = v.label
        }

    }



}