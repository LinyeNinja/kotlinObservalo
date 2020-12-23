package com.example.kotlinobservalo.KotlinLlamadas.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.kotlinobservalo.KotlinLlamadas.ListaContactos.contactos
import com.example.kotlinobservalo.KotlinLlamadas.objetos.Contacto
import com.example.kotlinobservalo.Paint
import com.example.kotlinobservalo.R
import com.example.kotlinobservalo.TinyDB
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FragmentAgregar : Fragment() {

    private lateinit var v: View
    private lateinit var btnAgregarContacto: Button
    private lateinit var nombreContacto: EditText
    private lateinit var numeroContacto: EditText
    private var elColorEstaBien = false
    private lateinit var nombre: String
    private lateinit var numero: String
    private lateinit var color: String
    private lateinit var mContext: Context
    private var colorAnterior: String = "#F04C29"
    private val posiblesColores = arrayListOf(
        "#F3F3F3",
        "#45B6FE",
        "#F04C29",
        "#78C422",
        "#F1FC53",
        "#FFB41F",
        "#B854FF"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        v =  inflater.inflate(R.layout.llamadas_fragment_agregar, container, false)
        btnAgregarContacto = v.findViewById(R.id.botonAgregaContacto)
        nombreContacto = v.findViewById(R.id.editTextNombre)
        numeroContacto = v.findViewById(R.id.editTextNumero)

        //Obtiene la lista
        mContext = requireActivity()
        val tinydb = TinyDB(mContext)
        contactos = tinydb.getListaContactos("Contactos")

        return v
    }

    //Acá está lo relacionado a agregar el contacto
    override fun onStart() {
        super.onStart()

        //Inicializo SharedPreferences para el color anterior
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences("Color Anterior", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        //Setea el color anterior guardado
        colorAnterior = sharedPref.getString("Color Anterior", "default").toString()
        editor.apply()

        //Botón para agregar el contacto
        val gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = Paint.radio
        gradientDrawable.setColor(Color.argb(0xFF, 0x53, 0xED, 0x72))

        btnAgregarContacto.background = gradientDrawable
        btnAgregarContacto.setOnClickListener {

            //Toma los EditText
            nombre = nombreContacto.text.toString().toUpperCase(Locale.ROOT)
            numero = numeroContacto.text.toString()

            //Selecciona y chequea el color
            elegirColor()
            chequearColor()

            //Guarda el nuevo color anterior
            editor.putString("Color Anterior", colorAnterior)
            editor.apply()

            //Lo pasa si los espacios no están vacios
            if (nombre.isNotEmpty() && numero.isNotEmpty() && elColorEstaBien) {

                //Acorta la string de ser muy larga
                if (nombre.length > 13) {
                    nombre = nombre.substring(0, 13) + "..."
                }

                //Escondo el teclado
                hideSoftKeyboard(requireActivity())

                //Agrega el contacto
                contactos.add(Contacto(nombre, numero, R.drawable.vacio, color))

                //Para la próxima
                elColorEstaBien = false

                //Guarda en TinyDB
                mContext = requireActivity()
                val tinydb = TinyDB(mContext)
                tinydb.putListaContactos("Contactos", contactos)

                //Vuelve al menú
                val action21 = FragmentAgregarDirections.actionFragmentAgregarToFragmentMenu()
                v.findNavController().navigate(action21)
            }

            //Esto existe por las dudas
            else if (!elColorEstaBien) {
                elegirColorDistinto()
            }

            //En caso de estar vacío alguno de los espacios
            else {
                Snackbar.make(v, "Debe escribir algo...", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //Elige un color
    private fun elegirColor () {
        posiblesColores.shuffle()
        color = posiblesColores[1]
    }

    //Comprueba la validez del color
    private fun chequearColor () {
        //Si está bien genial
        if (color != colorAnterior) {
            colorAnterior = color
            elColorEstaBien = true
        }

        //Sino elige uno distinto
        else { elegirColorDistinto() }

        return
    }

    //Vuelve a seleccionar un color
    private fun elegirColorDistinto () {
        elegirColor()
        chequearColor()
        return
    }

    //Función para esconder el teclado
    private fun hideSoftKeyboard(activity: Activity) {
        if (activity.currentFocus == null){
            return
        }
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }

}