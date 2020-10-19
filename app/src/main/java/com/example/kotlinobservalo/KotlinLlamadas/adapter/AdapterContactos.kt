package com.example.kotlinobservalo.KotlinLlamadas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.KotlinLlamadas.objetos.Contacto
import com.example.kotlinobservalo.R

class AdapterContactos (var listaContactos : MutableList<Contacto>, val onItemClick : (Int) -> Unit) : RecyclerView.Adapter<AdapterContactos.ContactosHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactosHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.llamadas_itemcontacto, parent, false)
        return ContactosHolder(view)
    }

    //Cuenta los items de la lista
    override fun getItemCount(): Int {
        return listaContactos.size
    }

    override fun onBindViewHolder(holder: AdapterContactos.ContactosHolder, position: Int) {
        //Detecta la imágen y el ícono de cada ítem
        holder.setNombre(listaContactos[position].nombre)
        holder.setIcono(listaContactos[position].icono)

        //OnClick
        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }
    }

    fun setData(newData: ArrayList<Contacto>) {
        this.listaContactos = newData
        this.notifyDataSetChanged()
    }

    //Muestra el nombre y el ícono apropiadamente
    class ContactosHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v

        fun setNombre(name: String) {
            val txt: TextView = view.findViewById(R.id.txtNombre)
            txt.text = name
        }

        fun setIcono(icon: Int) {
            val ico: ImageView = view.findViewById(R.id.imgIcono);
            ico.setImageResource(icon)
        }

        fun getCardLayout (): CardView {
            return view.findViewById(R.id.cardView)
        }

    }

}
