package com.example.kotlinobservalo.KotlinLlamadas.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.KotlinLlamadas.objetos.Contacto
import com.example.kotlinobservalo.Paint
import com.example.kotlinobservalo.R

class AdapterContactos(
    private var listaContactos: ArrayList<Contacto>,
    val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AdapterContactos.ContactosHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactosHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.llamadas_itemcontacto, parent, false)
        return ContactosHolder(view)
    }

    //Cuenta los items de la lista
    override fun getItemCount(): Int {
        return listaContactos.size
    }

    fun removeAt(position: Int) {
        listaContactos.removeAt(position)
        notifyItemRemoved(position)
    }

    fun onItemMove(sourcePosition: Int, targetPosition: Int) {
        //listaContactos.add(targetPosition, listaContactos.removeAt(sourcePosition))
        notifyItemMoved(sourcePosition, targetPosition)
    }

    override fun onBindViewHolder(holder: ContactosHolder, position: Int) {
        //Detecta la imágen y el ícono de cada ítem
        holder.setNombre(listaContactos[position].nombre)
        holder.setNumero(listaContactos[position].numero)
        holder.setIcono(listaContactos[position].icono)
        holder.setColor(listaContactos[position].color)

        //OnClick
        holder.getCardLayout().setOnClickListener {
            onItemClick(position)
        }
    }

    //Muestra el nombre y el ícono apropiadamente
    class ContactosHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v

        fun setNombre(name: String) {
            val txt: TextView = view.findViewById(R.id.txtNombre)
            txt.text = name
        }

        fun setIcono(@DrawableRes icon: Int) {
            val ico: ImageView = view.findViewById(R.id.imgIcono)
            ico.setImageResource(icon)
        }

        fun setNumero(numero: String) {
            val num: TextView = view.findViewById(R.id.txtNumero)
            num.text = numero
        }

        fun setColor(color: String) {
            val card: CardView = view.findViewById(R.id.cardView)

            card.radius = Paint.radio

            card.setCardBackgroundColor(Color.parseColor(color))
        }

        fun getCardLayout (): CardView {
            return view.findViewById(R.id.cardView)
        }

    }

}

