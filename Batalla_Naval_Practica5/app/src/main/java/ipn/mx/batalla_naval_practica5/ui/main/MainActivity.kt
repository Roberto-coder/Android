package ipn.mx.batalla_naval_practica5.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ipn.mx.batalla_naval_practica5.R
import ipn.mx.batalla_naval_practica5.ui.game.GameActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencia al botón de iniciar juego
        val startGameButton: Button = findViewById(R.id.startGameButton)

        // Configurar el Intent para redirigir a GameActivity
        startGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }
}