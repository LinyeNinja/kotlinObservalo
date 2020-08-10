package com.example.kotlinobservalo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
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
            val icon: Drawable = ri.activityInfo.loadIcon(pm)
            val version = pm.getPackageInfo(packageName, 0).versionName

            var color = Paint.colorApp(icon)

            val app = AppInfo(null, label, packageName, icon, color)
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
            Configs.cambiado = true
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