package com.example.kotlinobservalo.Popups

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinobservalo.*
import com.example.kotlinobservalo.ClasesDeInfo.AppInfo
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Config.Configs.modoConfig
import com.example.kotlinobservalo.Paint.appHeight
import com.example.kotlinobservalo.Paint.colorCarpetaAbierta


class DialogCarpeta(label: String, position: Int, listaCarpeta: MutableList<AppInfo>?, val onConfigHappened : (String, String) -> Unit): DialogFragment() {

    private var listaCarpeta: MutableList<AppInfo>? = null
    var label: String
    private var position: Int
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
    lateinit var v: View

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(modoConfig) {
            val editText: EditText = v.findViewById(R.id.tituloCarpeta)
            val texto = editText.text.toString()
            onConfigHappened("tituloCarpetaEditada$position", texto)
            Log.d("el texto se cambió a:", texto)
        }
    }

    /*
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return super.onCreateDialog(savedInstanceState)
    }
     */

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            getDialog()!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            //dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        v = inflater.inflate(R.layout.carpeta_abierta, container, false)

        //TODO intentos de hacer que el ancho sea el máx: NADA DE ESTAS COSAS HACE LO MÁS MÍNIMO SOBRE AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        //v.minimumWidth = Paint.anchuraDeLaPantalla
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

        val casaBackground = GradientDrawable()
        casaBackground.cornerRadius = Paint.radio
        casaBackground.setColor(colorCarpetaAbierta())
        val casa = v.findViewById<LinearLayout>(R.id.casaDeCarpeta)
        casa.background = casaBackground

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
        /*
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
        */
    }
    else{
        editText.isEnabled = false
    }
    editText.setTextColor(Paint.colorAppLabel())

    carpetaRecycler.addItemDecoration(itemDecoCarpeta)
        fun onConfigHappenedInAppAdapter(evento: String, packageName: String){
            onConfigHappened(evento, packageName)
            adaptadorCarpeta.notifyDataSetChanged() //TODO peligroso
            if(adaptadorCarpeta.items!!.size <= 0){
                dialog?.dismiss() //TODO peligroso
            }
        }

        adaptadorCarpeta = AppAdapter(listaCarpeta){ evento, pos -> onConfigHappenedInAppAdapter(evento, pos) }

        carpetaRecycler.layoutManager = layoutManagerCarpeta
        carpetaRecycler.adapter = adaptadorCarpeta

        return v
    }

}