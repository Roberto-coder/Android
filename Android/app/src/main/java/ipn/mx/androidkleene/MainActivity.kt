package ipn.mx.androidkleene

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

/**
 * MainActivity es la actividad principal de la aplicación que contiene
 * la interfaz de usuario y la lógica para abrir una página web en el navegador y cerrar la aplicación.
 */
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

    /**
     * Abre la página del servidor en el navegador web predeterminado.
     */
    private fun openServerPage() {
        val url = "http://192.168.100.13:8080"  // Cambiar URL si es necesario
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

/**
 * RequestScreen es una función composable que representa la pantalla principal de la aplicación.
 * Incluye campos para ingresar un número, seleccionar una operación y mostrar el resultado.
 *
 * @param onOpenServer Función a ejecutar cuando se presiona el botón para abrir el servidor.
 * @param onCloseApp Función a ejecutar cuando se presiona el botón para cerrar la aplicación.
 */
@Composable
fun RequestScreen(onOpenServer: () -> Unit, onCloseApp: () -> Unit) {
    var inputValue by remember { mutableStateOf(TextFieldValue("")) }
    var resultText by remember { mutableStateOf("Resultado aparecerá aquí") }
    var selectedOption by remember { mutableStateOf("cerradura") }  // Opción seleccionada: cerradura o estrella
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Botones para abrir el servidor o cerrar la aplicación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Botón para abrir navegador
            Button(
                onClick = onOpenServer,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text("Abrir Navegador")
            }

            Spacer(modifier = Modifier.width(16.dp))  // Espacio entre los botones

            // Botón para cerrar la aplicación
            Button(
                onClick = { onCloseApp() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.Black
                )
            ) {
                Text("Cerrar App")
            }
        }

        // Texto y opciones para seleccionar la operación
        Text(
            text = "Seleccionar operación",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        // Componente RadioButton para seleccionar la operación
        RadioButtonGroup { option ->
            selectedOption = option
        }

        // Campo de entrada para ingresar un número
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

        // Área para mostrar el resultado de la solicitud
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .verticalScroll(scrollState)
                .padding(8.dp)
        ) {
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Envía una solicitud HTTP GET al servidor con la opción seleccionada y un número ingresado.
 *
 * @param number El número ingresado por el usuario.
 * @param option La operación seleccionada ("cerradura" o "estrella").
 * @param onResult Función para manejar la respuesta de la solicitud.
 */
fun sendRequest(number: Int, option: String, onResult: (String) -> Unit) {
    val url = "http://192.168.100.13:8080/api/operaciones/$option/$number"  // URL con la opción seleccionada

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
                        val jsonObject = JSONObject(responseData)
                        val resultBuilder = StringBuilder()

                        // Iterar sobre las claves del JSON y obtener los valores
                        jsonObject.keys().forEach { key ->
                            val value = jsonObject.get(key)
                            resultBuilder.append("$key: $value\n")
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

/**
 * RadioButtonGroup es un componente que permite seleccionar entre las opciones
 * "cerradura" y "estrella".
 *
 * @param onSelectionChanged Función que se ejecuta cuando cambia la selección.
 */
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

/**
 * Vista previa para mostrar cómo se verá la aplicación en el editor de Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidKleeneTheme {
        RequestScreen({}, {})
    }
}
