package com.example.kotlinobservalo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    var lista:RecyclerView? = null
    var layoutManager:RecyclerView.LayoutManager? = null
    var adaptador:AppAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Contexto.mainActivity = this
        Contexto.app = this.applicationContext

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listaDeApps = AppGetter.getListaDeApps(this.applicationContext)

        lista = findViewById(R.id.lista)
        layoutManager = GridLayoutManager(this, 6, RecyclerView.HORIZONTAL, false)

        //var snapHelper:PagerSnapHelper = PagerSnapHelper()
        //snapHelper.attachToRecyclerView(lista)

        adaptador = AppAdapter(listaDeApps!!)
        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador
    }

}


//mesi