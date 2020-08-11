package com.example.kotlinobservalo.KotlinLlamadas.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.KotlinLlamadas.adapter.AdapterContactos
import com.example.kotlinobservalo.KotlinLlamadas.objetos.Contacto
import com.example.kotlinobservalo.R
import com.google.android.material.snackbar.Snackbar

class FragmentMenu : Fragment() {

    lateinit var v: View
    var flagAdd: Boolean = true
    var flagCall: Boolean = false
    var contactos: MutableList<Contacto> = ArrayList()
    lateinit var recyclerContacto: RecyclerView

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var contactosListAdapter: AdapterContactos

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.llamadas_fragment_menu, container, false)
        recyclerContacto = v.findViewById(R.id.recycler)

        if (flagAdd) {
            //Items de la lista por defecto
            contactos.add(Contacto("EMERGENCIAS", 107, R.drawable.ic_baseline_local_taxi_24))
            contactos.add(Contacto("POLICÍA", 911, R.drawable.ic_baseline_local_pizza_24))
            contactos.add(Contacto("BOMBEROS", 100, R.drawable.ic_baseline_fireplace_24))
            contactos.add(Contacto("AGREGAR", null, R.drawable.ic_baseline_add_24))
            flagAdd = false
        }

        recyclerContacto.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerContacto.layoutManager = linearLayoutManager
        contactosListAdapter = AdapterContactos(contactos!!) { position -> onItemClick(position) }
        recyclerContacto.adapter = contactosListAdapter

        return v
    }

    fun onItemClick(position: Int) {
        checkPermission()
        if(flagCall) {
            startCall(position, contactos)
            Snackbar.make(v, "mesi${position + 1}", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(v, "capo, necesitamos ese permiso", Snackbar.LENGTH_SHORT).show()

            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)) {
                //Pide permiso
                Snackbar.make(v, "aceptá porfa", Snackbar.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 42)
            }

            else {
                //Pide permiso
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 42)
            }
        }

        else {
            //Hay permiso
            flagCall = true
            Snackbar.make(v, "bien, permiso dado", Snackbar.LENGTH_SHORT).show()
        }

        return

    }

    //Llama si los permisos fueron aceptados
    fun startCall(position: Int, listaContactos: MutableList<Contacto>) {
        if (flagCall){
            //Cambiar el DIAL por call para que llame
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${listaContactos[position].numero}"))
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
            startActivity(intent)
        }

    }

}