package sv.edu.controlgastos

import android.app.Application
import com.google.firebase.FirebaseApp

class ControlGastosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}