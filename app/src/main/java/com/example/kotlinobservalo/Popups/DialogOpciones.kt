package com.example.kotlinobservalo.Popups

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.Botones.Btns
import com.example.kotlinobservalo.Botones.btnAdapter
import com.example.kotlinobservalo.Contexto
import com.example.kotlinobservalo.MainActivity
import com.example.kotlinobservalo.Paint
import com.example.kotlinobservalo.R
import java.util.*

class DialogOpciones(private val listaBtns: ArrayList<Btns>, val onButtonClick : (Int) -> Unit): DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val v: View = inflater.inflate(R.layout.lista_con_opciones, container, false)

        // Get the widgets reference from custom view
        val opcionesRecycler: RecyclerView = v.findViewById(R.id.listaDeBtns)
        val params: ViewGroup.LayoutParams = opcionesRecycler.layoutParams
        val separacion = 5
        params.width = Paint.anchuraDeLaPantalla / 10 * 7 //TODO ¿Debería ser esto así o adaptarse al tamaño del ícono más grande?

        //val layoutManagerOpciones = GridLayoutManager(Contexto.mainActivity, cantColumnas, RecyclerView.VERTICAL, false)
        val layoutManagerOpciones = LinearLayoutManager(Contexto.mainActivity, LinearLayoutManager.VERTICAL, false)

        val itemDecoCarpeta =
            MainActivity.ItemOffsetDecoration(
                separacion
            )
        opcionesRecycler.addItemDecoration(itemDecoCarpeta)


        val adaptadorOpciones = btnAdapter(listaBtns) { position -> onItemClick(position) }

        opcionesRecycler.layoutManager = layoutManagerOpciones
        opcionesRecycler.adapter = adaptadorOpciones

        return v
    }

    private fun onItemClick(position: Int) {
        Log.d("posicion", position.toString())
        onButtonClick(position)
        dismiss()
    }

}