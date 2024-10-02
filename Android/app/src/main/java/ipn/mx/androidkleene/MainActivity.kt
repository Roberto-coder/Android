package ipn.mx.androidkleene

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    RequestScreen(
                        onOpenServer = { openServerPage() },  // Pasamos la función para abrir el navegador
                        onCloseApp = { finish() }             // Pasamos la función para cerrar la app
                    )
                }
            }
        }
    }

    // Función para abrir la página del servidor en el navegador
    private fun openServerPage() {
        val url = "http://127.0.0.1:8080"  // Cambiar  URL si es necesario
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

@Composable
fun RequestScreen(onOpenServer: () -> Unit, onCloseApp: () -> Unit) {
    // Estado para almacenar el valor ingresado por el usuario y el resultado
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }
    var resultText by remember { mutableStateOf("Resultado aparecerá aquí") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Opcional: Espaciado entre los botones
        ) {
            // Botón para abrir navegador
            Button(
                onClick = onOpenServer ,
                modifier = Modifier.weight(1f), // Ocupa el 50% del espacio disponible
                colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray, // Color de fondo
                contentColor = Color.White  // Color del texto
                )
            ) {
                Text("Abrir Navegador")
            }

            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre los botones

            // Botón para cerrar la aplicación
            Button(
                onClick = { onCloseApp() },
                modifier = Modifier.weight(1f), // Ocupa el 50% del espacio disponible
                colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red, // Color de fondo
                contentColor = Color.Black  // Color del texto
                )
            ) {
                Text("Cerrar App")
            }
        }

        Text(
            text = "Seleccionar operación",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        // RadioButtonGroup
        var selectedOption by remember { mutableStateOf("cerradura") }
        RadioButtonGroup { option ->
            selectedOption = option
        }

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
                    sendRequest(number, selectedOption) { result ->
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

        Spacer(modifier = Modifier.height(16.dp))

    }
}

fun sendRequest(number: Int, option: String, onResult: (String) -> Unit) {
    //val url = "http://192.168.x.x:8080/api/operaciones/$option/$number"  // URL con la opción seleccionada
    val url = "http://127.0.0.1:8080/api/operaciones/$option/$number"

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error en la solicitud: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    try {
                        // Parsear el string de respuesta como JSON
                        val jsonObject = JSONObject(responseData)

                        // Crear un StringBuilder para acumular los elementos del JSON
                        val resultBuilder = StringBuilder()

                        // Iterar sobre las claves y obtener los valores
                        jsonObject.keys().forEach { key ->
                            val value = jsonObject.get(key)
                            resultBuilder.append("$key: $value\n") // Añadir cada clave-valor al resultado
                        }

                        // Devolver el resultado formateado
                        onResult(resultBuilder.toString())
                    } catch (e: Exception) {
                        onResult("Error al parsear JSON: ${e.message}")
                    }
                } else {
                    onResult("Error: respuesta vacía")
                }
            } else {
                onResult("Error: ${response.code}")
            }
        }

    })
}

@Composable
fun RadioButtonGroup(onSelectionChanged: (String) -> Unit) {
    var selectedOption by remember { mutableStateOf("cerradura") }

    Column {
        Row(Modifier.padding(8.dp)) {
            RadioButton(
                selected = selectedOption == "cerradura",
                onClick = {
                    selectedOption = "cerradura"
                    onSelectionChanged("cerradura")
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cerradura")
        }

        Row(Modifier.padding(8.dp)) {
            RadioButton(
                selected = selectedOption == "estrella",
                onClick = {
                    selectedOption = "estrella"
                    onSelectionChanged("estrella")
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Estrella")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidKleeneTheme {
        RequestScreen({}, {})
    }
}
