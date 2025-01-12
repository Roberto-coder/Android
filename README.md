# Battleship Game
Este proyecto es un juego de Batalla Naval implementado usando WebSockets para la comunicación en tiempo real entre el servidor y los clientes. El servidor está escrito en JavaScript usando Node.js, y el cliente es una aplicación Android escrita en Kotlin.  
Estructura del Proyecto

- **server.js**: El archivo principal del servidor que maneja las conexiones WebSocket y la lógica del juego.
- **GameActivity.kt**: La actividad principal para el cliente Android, responsable de renderizar el tablero de juego y manejar las interacciones del usuario.
- **GameViewModel.kt**: El ViewModel para el cliente Android, responsable de gestionar el estado del juego y comunicarse con el servidor.
- **GameWebSocketClient.kt**: El cliente WebSocket para la aplicación Android.

## Requisitos Previos
Node.js instalado en tu máquina.
Android Studio instalado para ejecutar el cliente Android.
Configuración
Servidor
Navega al directorio del servidor:  
cd Android/Servidor_BN
Instala las dependencias requeridas:  
npm install
Inicia el servidor:  
node server.js
El servidor comenzará a ejecutarse en ws://localhost:3000.  
Cliente Android
Abre el proyecto en Android Studio.  
Compila y ejecuta la aplicación en un emulador o dispositivo físico.  

## Reglas del Juego
Cada jugador coloca sus barcos en un tablero de 6x6.
Los jugadores se turnan para disparar misiles al tablero del oponente.
El juego termina cuando todos los barcos de un jugador son hundidos.
El servidor notificará a los clientes cuando el juego haya terminado, indicando el ganador y el perdedor.

## Protocolo de Comunicación
El servidor y los clientes se comunican usando mensajes JSON a través de WebSockets. Las siguientes acciones son soportadas:
CONNECT: Enviado por el cliente para unirse al juego.
END_TURN: Enviado por el cliente para terminar su turno.
FIRE_MISSILE: Enviado por el cliente para disparar un misil al tablero del oponente.
UPDATE_FLAGS: Enviado por el servidor para actualizar el estado del juego para los clientes.
GAME_OVER: Enviado por el servidor para notificar a los clientes que el juego ha terminado.

# Wearables App

This is a Wear OS application developed using Kotlin and Java. The project is built with Gradle and uses various libraries to enhance functionality.

## Features

- **Wear OS Support**: The app is designed specifically for Wear OS devices.
- **Retrofit**: For network operations.
- **Glide**: For image loading.
- **Compose**: For modern UI development.

## Requirements

- Android Studio Koala Feature Drop | 2024.1.2
- Minimum SDK: 34
- Target SDK: 34

## Setup

1. **Clone the repository**:
    ```sh
    git clone https://github.com/Roberto-coder/wearables.git
    cd wearables
    ```

2. **Open the project in Android Studio**:
    - Open Android Studio.
    - Select `Open an existing project`.
    - Navigate to the cloned repository and select it.

3. **Build the project**:
    - Click on `Build` > `Make Project` or press `Ctrl+F9`.
