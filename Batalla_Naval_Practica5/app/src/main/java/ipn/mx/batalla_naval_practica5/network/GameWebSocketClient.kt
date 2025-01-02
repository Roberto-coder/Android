import android.util.Log
import ipn.mx.batalla_naval_practica5.ui.game.GameActivity
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.net.URI

class GameWebSocketClient(private val activity: GameActivity, serverUri: URI) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("WebSocket", "Connected to server")
        val playerName = activity.getPlayerName()
        val message = JSONObject().apply {
            put("action", "CONNECT")
            put("playerName", playerName)
        }
        send(message.toString())
    }

    override fun onMessage(message: String?) {
        Log.d("WebSocket", "Message received: $message")
        message?.let {
            val json = JSONObject(it)
            handleClientMessage(json)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("WebSocket", "Disconnected from server: $reason")
        reconnect()
    }

    override fun onError(ex: Exception?) {
        Log.e("WebSocket", "Error: ${ex?.message}")
        reconnect()
    }

    override fun reconnect() {
        Log.d("WebSocket", "Attempting to reconnect...")
        try {
            Thread.sleep(5000) // Wait for 5 seconds before reconnecting
            val newWebSocketClient = GameWebSocketClient(activity, uri)
            activity.setWebSocketClient(newWebSocketClient)
            newWebSocketClient.connect()
        } catch (e: InterruptedException) {
            Log.e("WebSocket", "Reconnection interrupted: ${e.message}")
        }
    }

    private fun handleClientMessage(json: JSONObject) {
        when (json.getString("action")) {
            "INITIAL_GAME_STATE" -> {
                val gameState = json.getJSONObject("gameState")
                activity.runOnUiThread {
                    activity.showMessage("Initial game state received")
                    activity.updateGameState(gameState.toString())
                    activity.saveGameState(gameState.toString())
                }
            }
            "UPDATE_FLAGS" -> {
                val gameState = json.getJSONObject("gameState")
                val flag = json.getString("flag")
                activity.runOnUiThread {
                    activity.updateGameState(gameState.toString())
                    activity.handleGameState(flag)
                }
            }
            "FIRE_MISSILE" -> {
                val gameState = json.getJSONObject("gameData")
                activity.runOnUiThread {
                    activity.updateGameState(gameState.toString())
                    activity.saveGameState(gameState.toString())
                    activity.drawBoardState() // Redraw the board state
                }
            }
            "ERROR" -> {
                val errorMessage = json.getString("message")
                activity.runOnUiThread {
                    activity.showMessage(errorMessage)
                }
            }
            else -> {
                activity.runOnUiThread {
                    activity.showMessage(json.toString())
                }
            }
        }
    }
}