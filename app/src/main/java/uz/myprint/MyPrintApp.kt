package uz.myprint

import android.app.Application
import uz.myprint.core.di.AppContainer

/**
 * Ilova kirish nuqtasi.
 *
 * Yagona vazifasi — AppContainer ga ilova kontekstini berish.
 * Loyihalar ombori fayl tizimi bilan ishlaydi, unga esa kontekst
 * kerak, lekin uni har ekranda uzatib yurish kodni ifloslantiradi.
 *
 * AndroidManifest.xml da ro'yxatdan o'tkazish SHART:
 *
 *   <application
 *       android:name=".MyPrintApp"
 *       ... >
 *
 * Aks holda bu sinf umuman ishga tushmaydi va birinchi saqlashda
 * "lateinit property appContext has not been initialized" xatosi
 * chiqadi.
 */
class MyPrintApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
