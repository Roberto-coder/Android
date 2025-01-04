package ipn.mx.wearables.presentation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ipn.mx.wearables.R

class WearableActivity : AppCompatActivity() {

    private lateinit var btnOmnitrixMode: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wearable)

        btnOmnitrixMode = findViewById(R.id.btnOmnitrixMode)

        // Set up button click listener
        btnOmnitrixMode.setOnClickListener {
            // Activate Omnitrix mode
        }
    }

    private fun showCurrentMeme() {
        // Display the current meme
    }
}