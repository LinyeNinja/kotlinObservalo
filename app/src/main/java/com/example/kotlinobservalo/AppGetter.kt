package com.example.kotlinobservalo

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import java.util.*


object AppGetter {
    //La siguiente función devuelve la lista de aplicaciones del sistema
    //Cada aplicación está guardada como un "AppInfo" (clase nuestra)
    //Los datos por ahora son: título, nombre de paquete e ícono, aunque también debería tener uno para el color de fondo
    fun getListaDeApps(c: Context): ArrayList<AppInfo>? {
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

            val app = AppInfo(label, packageName, icon)
            appsList.add(app)
        }
        return appsList
    }
}