package com.example.kotlinobservalo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Config.LclObservaloConfigActivity
import java.util.*


object AppGetter {
    //La siguiente función devuelve la lista de aplicaciones del sistema
    //Cada aplicación está guardada como un "AppInfo" (clase nuestra)
    //Los datos por ahora son: título, nombre de paquete e ícono, aunque también debería tener uno para el color de fondo
    fun getListaDeApps(c: Context): ArrayList<AppInfo> {
        val pm = c.packageManager
        val appsList = ArrayList<AppInfo>()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(i, 0)

        //Lo siguiente es un for que pasa por todas las aplicaciones y crea un coso para cada una
        for (ri in allApps) {
            val label = ri.loadLabel(pm).toString()
            val packageName = ri.activityInfo.packageName
            val icon = ri.activityInfo.loadIcon(pm)

            var color = Color.RED
            if (Configs.obtenerBoolean("modoAltoContraste") == true){
                if (Configs.obtenerBoolean("modoNoche") == true){
                    color = Color.BLACK
                }
                else{
                    color = Color.WHITE
                }
            }
            else{
                color = Paint.getDominantFlatColor(icon)
            }
            if (Configs.obtenerBoolean("modoSepia") == true){
                color += 704214
            }


            val app = AppInfo(label, packageName, icon, color)
            appsList.add(app)
        }
        return appsList
    }


    fun launch(packageName:String){
        Log.d("a", packageName)
        if (packageName.contains("LclObservalo")) { //!!!!Esto tiene un problema!!!! por alguna razón no funciona cuando intento pedirle que me convierta uan String a una clase, por lo que ahorita mismo esto solo puede abrir la configuracion
            //val intent = Intent(Contexto.mainActivity, packageName::class.java)
            /*try {
                val c = Class.forName(packageName)
                val intent = Intent(Contexto.mainActivity, c)
                Contexto.mainActivity.startActivity(intent)
            } catch (ignored: ClassNotFoundException) {
            }   */
            val intent = Intent(Contexto.mainActivity, LclObservaloConfigActivity::class.java)
            Contexto.mainActivity.startActivity(intent)
        }
        else {
            val context = Contexto.app
            val launchAppIntent: Intent? = context.getPackageManager()
                .getLaunchIntentForPackage(packageName)
            if (launchAppIntent != null) context.startActivity(launchAppIntent)
        }
    }
}