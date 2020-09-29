package com.example.kotlinobservalo.Popups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.*
import com.example.kotlinobservalo.ClasesDeInfo.AppInfo
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Config.Configs.modoConfig
import com.example.kotlinobservalo.Paint.appHeight


class DialogCarpeta(label: String, position: Int, listaCarpeta: MutableList<AppInfo>?, val onConfigHappened : (String, String) -> Unit): DialogFragment() {

    var listaCarpeta: MutableList<AppInfo>? = null
    var label: String
    var position: Int
    init {
        this.listaCarpeta = listaCarpeta
        this.label = label
        this.position = position
    }
/*
    override fun getTheme(): Int {
        return R.style.CarpetaTheme
    }
*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v: View = inflater.inflate(R.layout.carpeta_abierta, container, false)

        //TODO intentos de hacer que el ancho sea el máx: NADA DE ESTAS COSAS HACE LO MÁS MÍNIMO SOBRE AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        //v.minimumWidth = Paint.anchuraDeLaPantalla
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT
        )
        /*
        val p = dialog!!.window!!.attributes
        p.gravity = Gravity.FILL_HORIZONTAL
        getDialog()!!.getWindow()!!.setLayout(320,320);
        dialog!!.window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog!!.window!!.attributes = p
        */
        // Get the widgets reference from custom view
        val carpetaRecycler: RecyclerView = v.findViewById(R.id.listaCarpeta)
        val params: ViewGroup.LayoutParams = carpetaRecycler.layoutParams
        val separacion = 5
        val cantColumnas = Configs.cantColumnas()
        params.height = cantColumnas*appHeight+separacion*cantColumnas*2
        params.width = Paint.anchuraDeLaPantalla

        val layoutManagerCarpeta = GridLayoutManager(Contexto.mainActivity, cantColumnas, RecyclerView.VERTICAL, false)

        val itemDecoCarpeta =
            MainActivity.ItemOffsetDecoration(
                separacion
            )
        lateinit var adaptadorCarpeta: AppAdapter

        //el edit text:
    val editText: EditText = v.findViewById(R.id.tituloCarpeta)
    editText.setText(label)
    if(modoConfig){
         val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            Log.d("el texto se cambió a:", s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //output.text = s
            label = s.toString()
            onConfigHappened("tituloCarpetaEditada" + position.toString(), label)
            Log.d("el texto se cambió a:", s.toString())
            /*
            if (start == 12) {
                Toast.makeText(applicationContext, "Maximum Limit Reached", Toast.LENGTH_SHORT)
                    .show()
            }
             */
        }
    }
        editText.addTextChangedListener(textWatcher)
    }
    else{
        editText.setEnabled(false)
    }



    carpetaRecycler.addItemDecoration(itemDecoCarpeta)
        fun onConfigHappenedInAppAdapter(evento: String, packageName: String){
            onConfigHappened(evento, packageName)
            adaptadorCarpeta.notifyDataSetChanged() //TODO peligroso
            if(!(adaptadorCarpeta.items!!.size > 0)){
                dialog?.dismiss() //TODO peligroso
            }
        }

        adaptadorCarpeta = AppAdapter(listaCarpeta){ evento, pos -> onConfigHappenedInAppAdapter(evento, pos) }

        carpetaRecycler.layoutManager = layoutManagerCarpeta
        carpetaRecycler.adapter = adaptadorCarpeta

        return v
    }

}