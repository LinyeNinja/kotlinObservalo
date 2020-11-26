package com.example.kotlinobservalo.KotlinLlamadas.fragments

import android.Manifest
import android.content.Context
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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.KotlinLlamadas.ListaContactos.contactos
import com.example.kotlinobservalo.TinyDB
import com.example.kotlinobservalo.KotlinLlamadas.adapter.AdapterContactos
import com.example.kotlinobservalo.KotlinLlamadas.objetos.Contacto
import com.example.kotlinobservalo.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class FragmentMenu : Fragment() {

    private lateinit var v: View
    private var flagCall: Boolean = false
    private lateinit var mContext: Context
    private lateinit var recyclerContacto: RecyclerView
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var contactosListAdapter: AdapterContactos
    private var fueAgregado = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.llamadas_fragment_menu, container, false)
        recyclerContacto = v.findViewById(R.id.recycler)
        btnAgregar = v.findViewById(R.id.fab_add_patient)
        recyclerContacto.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        recyclerContacto.layoutManager = linearLayoutManager

        //Consigue la lista al iniciar
        mContext = requireActivity()
        val tinydb = TinyDB(mContext)
        contactos = tinydb.getListaContactos("Contactos")
        fueAgregado = tinydb.getBoolean("Agregados")

        //Contactos por defecto
        if (contactos.size == 0 && !fueAgregado) {
            contactos.add(
                Contacto(
                    "EMERGENCIAS",
                    "107",
                    R.drawable.ic_baseline_local_taxi_24,
                    "#F3F3F3"
                )
            )
            contactos.add(
                Contacto(
                    "POLICÍA",
                    "911",
                    R.drawable.ic_baseline_local_pizza_24,
                    "#45B6FE"
                )
            )
            contactos.add(
                Contacto(
                    "BOMBEROS",
                    "100",
                    R.drawable.ic_baseline_fireplace_24,
                    "#F04C29"
                )
            )
            fueAgregado = true
            tinydb.putBoolean("Agregados", fueAgregado)
            tinydb.putListaContactos("Contactos", contactos)
        }

        //Adapter
        contactosListAdapter =  AdapterContactos(contactos) { position -> onItemClick(position) }
        recyclerContacto.adapter = contactosListAdapter

        ///////////////////////////////////////////Acá empieza lo de mover ítems///////////////////////////////////////////

        val touchHelper = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {

            //Al mover
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                contactosListAdapter.onItemMove(sourcePosition, targetPosition)
                val sourceAnterior = contactos[sourcePosition]
                contactos[sourcePosition] = contactos[targetPosition]
                contactos[targetPosition] = sourceAnterior
                return true
            }

            //Al deslizar
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                contactosListAdapter.removeAt(viewHolder.adapterPosition)
                tinydb.putListaContactos("Contactos", contactos)
            }

            //Al soltar
            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                when (actionState) { ItemTouchHelper.ACTION_STATE_IDLE -> {
                    contactosListAdapter.notifyDataSetChanged()
                    tinydb.putListaContactos("Contactos", contactos)
                }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(touchHelper)
        itemTouchHelper.attachToRecyclerView(recyclerContacto)

        ///////////////////////////////////////////Acá termina lo de mover ítems///////////////////////////////////////////

        return v
    }

    //Acá hay botones
    override fun onStart() {
        super.onStart()
        //El botón que te lleva al fragment para agregar contactos
        btnAgregar.setOnClickListener {
            val action = FragmentMenuDirections.actionFragmentMenuToFragmentAgregar()
            v.findNavController().navigate(action)
        }
    }

    ///////////////////////////////////////////Acá empiezan los permisos y llamadas///////////////////////////////////////////

    //Botón para llamar
    private fun onItemClick(position: Int) {
        checkPermission()
        if(flagCall) {
            startCall(position, contactos)
        }
    }

    //Chequea los permisos
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(v, "capo, necesitamos ese permiso", Snackbar.LENGTH_SHORT).show()

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                )) {
                //Pide permiso
                Snackbar.make(v, "aceptá porfa", Snackbar.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42
                )
            }

            else {
                //Pide permiso 2
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42
                )
            }
        }

        else {
            //Hay permiso
            flagCall = true
        }

        return

    }

    //Llama
    private fun startCall(position: Int, listaContactos: ArrayList<Contacto>) {
        if (flagCall){
            //Cambiar el DIAL por call para que llame
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:${listaContactos[position].numero}")
            )
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            startActivity(intent)
        }
    }

    ///////////////////////////////////////////Acá terminan los permisos y llamadas///////////////////////////////////////////

}