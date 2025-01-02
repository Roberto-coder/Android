package ipn.mx.batalla_naval_practica5.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ipn.mx.batalla_naval_practica5.R
import ipn.mx.batalla_naval_practica5.ui.game.GameActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            val playerName = nameEditText.text.toString()
            deleteLocalFiles()
            val intent = Intent(this, GameActivity::class.java).apply {
                putExtra("PLAYER_NAME", playerName)
            }
            startActivity(intent)
        }
    }

    private fun deleteLocalFiles() {
        val gameDataFile = File(filesDir, "gameData.json")
        if (gameDataFile.exists()) {
            gameDataFile.delete()
        }

        val gameStateDir = File(filesDir, "game_data")
        val gameStateFile = File(gameStateDir, "gameState.json")
        if (gameStateFile.exists()) {
            gameStateFile.delete()
        }
    }
}