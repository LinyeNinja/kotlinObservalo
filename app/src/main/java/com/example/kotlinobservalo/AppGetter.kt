package com.example.kotlinobservalo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony.Sms.getDefaultSmsPackage
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.example.kotlinobservalo.ClasesDeInfo.AppInfo
import com.example.kotlinobservalo.Config.Configs
import com.example.kotlinobservalo.Config.LclObservaloConfigActivity
import com.example.kotlinobservalo.KotlinLlamadas.LclObservaloLlamadas
import com.example.kotlinobservalo.Lupa.LclObservaloLupa
import java.util.*


lateinit var pm: PackageManager

object AppGetter {

    //La siguiente función devuelve la lista de aplicaciones del sistema
    //Cada aplicación está guardada como un "AppInfo" (clase nuestra)
    //Los datos por ahora son: título, nombre de paquete e ícono, aunque también debería tener uno para el color de fondo
    fun getListaDeApps(c: Context): ArrayList<AppInfo> {
        pm = c.packageManager
        val appsList = ArrayList<AppInfo>()
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = pm.queryIntentActivities(i, 0)

        val defaultSms = getDefaultApp("sms")
        val defaultContactos = getDefaultApp("contactos")
        val defaultLlamadas = getDefaultApp("llamadas")
        val defaultConfig = getDefaultApp("config")
        val defaultReloj = getDefaultApp("reloj")
        Log.d("default: ", defaultContactos)
        Log.d("default: ", defaultLlamadas)

        //Lo siguiente es un for que pasa por todas las aplicaciones y crea un coso para cada una
        for (ri in allApps) {
            val label = ri.loadLabel(pm).toString()
            val packageName = ri.activityInfo.packageName
            val version = pm.getPackageInfo(packageName, 0).versionName

            var color: Int

            val icon: Drawable
            if (packageName == defaultSms) {
                icon = ContextCompat.getDrawable(Contexto.mainActivity, R.drawable.ic_mensajes)!!
                color = Paint.colorObservaloApp("mensajes")
            }
            else{
                icon = ri.activityInfo.loadIcon(pm)
                color = Paint.colorApp(icon)
            }

            val app = AppInfo(
                null,
                label,
                packageName,
                icon,
                color
            )
            appsList.add(app)
        }

        return appsList
    }

    fun launch(packageName:String){
        Log.d("a", packageName)
        if (packageName.contains("LclObservalo")) { //!!!!Esto tiene un problema!!!! por alguna razón no funciona cuando intento pedirle que me convierta una String a una clase, por lo que ahorita mismo esto solo puede abrir la configuracion
            //val intent = Intent(Contexto.mainActivity, packageName::class.java)
            /*try {
                val c = Class.forName(packageName)
                val intent = Intent(Contexto.mainActivity, c)
                Contexto.mainActivity.startActivity(intent)
            } catch (ignored: ClassNotFoundException) {
            }   */
            if (packageName.contains("Config")){
                val intent = Intent(Contexto.mainActivity, LclObservaloConfigActivity::class.java)
                Configs.cambiado = true
                Contexto.mainActivity.startActivity(intent)
            }
            else if (packageName.contains("Llamadas")){
                val intent = Intent(Contexto.mainActivity, LclObservaloLlamadas::class.java)
                Contexto.mainActivity.startActivity(intent)
            }
            else if (packageName.contains("Lupa")){
                val intent = Intent(Contexto.mainActivity, LclObservaloLupa::class.java)
                Contexto.mainActivity.startActivity(intent)
            }
        }
        else {
            val context = Contexto.app
            val launchAppIntent: Intent? = context.getPackageManager()
                .getLaunchIntentForPackage(packageName)
            if (launchAppIntent != null) context.startActivity(launchAppIntent)
        }
    }

    fun uninstall(paquete: String){
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        intent.data = Uri.parse("package:" + paquete)
        Contexto.mainActivity.startActivity(intent)
    }

    private fun getDefaultApp(tipo: String): String{ //necesita alguna forma de prevenir errores
        when (tipo){
            "sms" -> return getDefaultSmsPackage(Contexto.mainActivity)
            "contactos" -> { //inventado, puede estar mal
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                val resolveInfoList = pm.queryIntentActivities(intent, 0)
                return resolveInfoList[0].activityInfo.packageName
            }
            "llamadas" -> {
            val intent = Intent(Intent.ACTION_DIAL).addCategory(Intent.CATEGORY_DEFAULT)
            val resolveInfoList = pm.queryIntentActivities(intent, 0)
            return resolveInfoList[0].activityInfo.packageName
            }
            "config" -> return getDefaultSmsPackage(Contexto.mainActivity)
            "reloj" -> return getDefaultSmsPackage(Contexto.mainActivity)
            else -> return "null"
        }
    }
}