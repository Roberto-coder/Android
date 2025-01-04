package ipn.mx.wearables.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import ipn.mx.wearables.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var btnNextMeme: Button
    private lateinit var imgMeme: ImageView
    private var memes: List<Meme>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgflip.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Set up UI components
        btnNextMeme = findViewById(R.id.btnNextMeme)
        imgMeme = findViewById(R.id.imgMeme)

        // Set up button click listener
        btnNextMeme.setOnClickListener {
            getRandomMeme()
        }

        // Set up image click listener
        imgMeme.setOnClickListener {
            getRandomMeme()
        }

        // Load initial meme
        getRandomMeme()
    }

    private fun getRandomMeme() {
        if (memes == null) {
            apiService.getMemes().enqueue(object : Callback<MemeResponse> {
                override fun onResponse(call: Call<MemeResponse>, response: Response<MemeResponse>) {
                    if (response.isSuccessful) {
                        memes = response.body()?.data?.memes
                        memes?.let {
                            val randomMeme = it.random()
                            Glide.with(this@MainActivity)
                                .load(randomMeme.url)
                                .into(imgMeme)
                        }
                    }
                }

                override fun onFailure(call: Call<MemeResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Failed to load meme", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            memes?.let {
                val randomMeme = it.random()
                Glide.with(this@MainActivity)
                    .load(randomMeme.url)
                    .circleCrop()
                    .into(imgMeme)
            }
        }
    }
}