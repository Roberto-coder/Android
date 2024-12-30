package ipn.mx.batalla_naval_practica5.ui.game

import GameWebSocketClient
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ipn.mx.batalla_naval_practica5.R
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.models.Ship
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI

class GameActivity : AppCompatActivity() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var myBoardGrid: GridLayout
    private lateinit var myShotsGrid: GridLayout
    private lateinit var webSocketClient: GameWebSocketClient
    private lateinit var playerName: String
    private lateinit var readyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Unknown"

        val serverUri = URI("ws://10.0.2.2:3000")
        webSocketClient = GameWebSocketClient(this, serverUri)

        gameViewModel = ViewModelProvider(this, GameViewModelFactory(applicationContext, webSocketClient)).get(GameViewModel::class.java)

        myBoardGrid = findViewById(R.id.myBoardGrid)
        myShotsGrid = findViewById(R.id.myShotsGrid)
        readyButton = findViewById(R.id.readyButton)

        createGrid(myBoardGrid, true)
        createGrid(myShotsGrid, false)

        readyButton.setOnClickListener {
            onReadyButtonClicked()
        }

        drawBoardState()
    }

    override fun onStart() {
        super.onStart()
        webSocketClient.connect()
    }

    override fun onStop() {
        super.onStop()
        webSocketClient.close()
    }

    private fun onReadyButtonClicked() {
        if (webSocketClient.isOpen) {
            if (gameViewModel.isTurn()) {
                // Ensure the directory exists
                val directory = File(filesDir, "game_data")
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                // Save the game state to a file
                val gameData = gameViewModel.loadGame()
                val gameStateJson = gameDataToJson(gameData)
                val file = File(directory, "gameData.json")
                file.writeText(gameStateJson)

                // Send the file to the server
                val message = mapOf("action" to "END_TURN", "playerName" to getPlayerName(), "gameData" to gameStateJson)
                webSocketClient.send(JSONObject(message).toString())
                showToast("Turn data sent")
            } else {
                showToast("Not your turn")
            }
        } else {
            showToast("WebSocket is not connected")
        }
    }

    private fun gameDataToJson(gameData: GameData): String {
        val jsonObject = JSONObject()
        jsonObject.put("myBoard", JSONArray(gameData.myBoard.map { JSONArray(it.toList()) }))
        jsonObject.put("myShotsBoard", JSONArray(gameData.myShotsBoard.map { JSONArray(it.toList()) }))
        jsonObject.put("shipsToPlace", JSONArray(gameData.shipsToPlace.map { ship ->
            JSONObject().apply {
                put("length", ship.length)
                put("isHorizontal", ship.isHorizontal)
            }
        }))
        jsonObject.put("currentPlayerIndex", gameData.currentPlayerIndex)
        jsonObject.put("isTurn", gameData.isTurn)
        jsonObject.put("gameState", gameData.gameState)
        return jsonObject.toString()
    }


    public fun handleGameState(state: String) {
        when (state) {
            "coloca tus barcos" -> enableShipPlacement()
            "espera tu turno" -> disableAllActions()
            "envia un misil" -> enableMissileFiring()
            "haz ganado", "haz perdido" -> showEndGameMessage(state)
        }
    }

    fun showMessage(message: String) {
        showToast(message)
        gameViewModel.updateGameState(message)
        handleGameState(message)
    }

    fun updateGameState(newState: String) {
        gameViewModel.updateGameState(newState)
    }

    private fun enableShipPlacement() {
        myBoardGrid.isEnabled = true
        myShotsGrid.isEnabled = false
    }

    private fun disableAllActions() {
        myBoardGrid.isEnabled = false
        myShotsGrid.isEnabled = false
    }

    private fun enableMissileFiring() {
        myBoardGrid.isEnabled = false
        myShotsGrid.isEnabled = true
    }

    private fun showEndGameMessage(message: String) {
        showToast(message)
    }

    private fun createGrid(grid: GridLayout, isShipPlacement: Boolean) {
        grid.removeAllViews()
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val imageView = ImageView(this).apply {
                    tag = Pair(row, col)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                    }
                    setBackgroundResource(R.drawable.grid_background)
                }

                imageView.setOnClickListener {
                    val message = if (isShipPlacement) {
                        placeShip(row, col, 3, gameViewModel.getNextShipOrientation())
                    } else {
                        gameViewModel.fireMissile(row, col)
                    }
                    showToast(message)
                    drawBoardState()
                }

                grid.addView(imageView)
            }
        }
    }

    private fun placeShip(row: Int, col: Int, length: Int, isHorizontal: Boolean): String {
        if (!gameViewModel.canPlaceMoreShips()) {
            return "Ya has colocado todos los barcos"
        }

        if (!gameViewModel.isValidPlacement(row, col, length, isHorizontal)) {
            return "No hay suficiente espacio para colocar el barco en esa posición"
        }

        val grid = myBoardGrid
        val imageView = ImageView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = if (isHorizontal) GridLayout.spec(col, length, 1f) else GridLayout.spec(col, 1f)
                rowSpec = if (isHorizontal) GridLayout.spec(row, 1f) else GridLayout.spec(row, length, 1f)
            }
            setImageResource(if (isHorizontal) R.drawable.barcohorizontal else R.drawable.barcovertical)
        }
        grid.addView(imageView)
        gameViewModel.placeShip(row, col, length, isHorizontal) // Update game state
        return "Ship placed at ($row, $col)"
    }

    private fun drawBoardState() {
        val gameData = gameViewModel.loadGame()
        drawGridState(myBoardGrid, gameData.myBoard)
        drawGridState(myShotsGrid, gameData.myShotsBoard)
    }

    private fun drawGridState(grid: GridLayout, board: Array<Array<Int>>) {
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val imageView = grid.getChildAt(row * 10 + col) as ImageView
                when (board[row][col]) {
                    0 -> imageView.setImageResource(0) // No image
                    1 -> imageView.setImageResource(R.drawable.misil)
                    2 -> imageView.setImageResource(R.drawable.barcohorizontal)
                }
            }
        }
    }

    fun getPlayerName(): String {
        return playerName
    }

    fun saveGameState(gameState: String) {
        val gameData = GameData(
            myBoard = Array(10) { Array(10) { 0 } },
            myShotsBoard = Array(10) { Array(10) { 0 } },
            shipsToPlace = listOf(
                Ship(length = 2, isHorizontal = false),
                Ship(length = 3, isHorizontal = false),
                Ship(length = 4, isHorizontal = true)
            ),
            currentPlayerIndex = 0,
            isTurn = true,
            gameState = gameState
        )
        gameViewModel.saveGame(gameData)
    }

    private fun showToast(message: String) {
        if (!isFinishing && !isDestroyed) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun setWebSocketClient(newClient: GameWebSocketClient) {
        webSocketClient = newClient
    }
}