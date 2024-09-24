package ipn.mx.androidkleene

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ipn.mx.androidkleene.ui.theme.AndroidKleeneTheme
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidKleeneTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    RequestScreen()
                }
            }
        }
    }
}


@Composable
fun RequestScreen() {
    // Estado para almacenar el valor ingresado por el usuario y el resultado
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }
    var resultText by remember { mutableStateOf("Resultado aparecerá aquí") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Campo de entrada de número
        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Ingrese un número") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para enviar la solicitud
        Button(
            onClick = {
                val number = inputValue.text.toIntOrNull()
                if (number != null) {
                    sendRequest(number) { result ->
                        resultText = result
                    }
                } else {
                    resultText = "Por favor, ingrese un número válido."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el resultado de la solicitud
        Text(
            text = resultText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun sendRequest(number: Int, onResult: (String) -> Unit) {
    // URL con el parámetro de la solicitud
    val url = "http://localhost:8002/operaciones"
    //val url = "https://example.com/api/resultado?numero=$number"
    //"val url = http://localhost:8002/operaciones/$number"

    // Crear la solicitud HTTP
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    // Realizar la solicitud de forma asincrónica
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error en la solicitud: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            //JSON
            /*if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    val json = JSONObject(responseData)
                    val resultado = json.getString("resultado") // Ajusta según la estructura de tu JSON
                    onResult("Resultado: $resultado")
                } else {
                    onResult("Error: respuesta vacía")
                }
            } else {
                onResult("Error: ${response.code}")
            }*/
            //XML,HTML, TEXTO, ETC
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    // Actualizar el estado de Compose de manera directa
                    onResult("Resultado: $responseData") // Llamar al callback para actualizar la UI
                } else {
                    onResult("Error: respuesta vacía")
                }
            } else {
                onResult("Error: ${response.code}")
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidKleeneTheme {
        RequestScreen()
    }
}

