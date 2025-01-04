package ipn.mx.batalla_naval_practica5.ui.game

import GameWebSocketClient
import android.content.Context
import androidx.lifecycle.ViewModel
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.models.Ship
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class GameViewModel(private val context: Context, private val webSocketClient: GameWebSocketClient) : ViewModel() {

    private val gameRepository = GameRepository(context)

    companion object {
        const val BOARD_SIZE = GameActivity.BOARD_SIZE // Use the same board size as in GameActivity
    }

    fun saveGame(gameData: GameData) {
        gameRepository.saveAsJson(gameData)
    }

    fun loadGame(): GameData {
        val gameDataJson = gameRepository.loadGameJson()
        val jsonObject = JSONObject(gameDataJson)

        val myBoard = if (jsonObject.has("myBoard")) {
            Array(BOARD_SIZE) { row ->
                Array(BOARD_SIZE) { col ->
                    jsonObject.getJSONArray("myBoard").getJSONArray(row).getInt(col)
                }
            }
        } else {
            Array(BOARD_SIZE) { Array(BOARD_SIZE) { 0 } }
        }

        val myShotsBoard = if (jsonObject.has("myShotsBoard")) {
            Array(BOARD_SIZE) { row ->
                Array(BOARD_SIZE) { col ->
                    jsonObject.getJSONArray("myShotsBoard").getJSONArray(row).getInt(col)
                }
            }
        } else {
            Array(BOARD_SIZE) { Array(BOARD_SIZE) { 0 } }
        }

        val shipsToPlace = if (jsonObject.has("shipsToPlace")) {
            jsonObject.getJSONArray("shipsToPlace").let { shipsArray ->
                List(shipsArray.length()) { index ->
                    val shipObject = shipsArray.getJSONObject(index)
                    Ship(shipObject.getInt("length"), shipObject.getBoolean("isHorizontal"))
                }
            }
        } else {
            listOf(
                Ship(length = 2, isHorizontal = false),
                Ship(length = 3, isHorizontal = false),
                Ship(length = 4, isHorizontal = true)
            )
        }

        return GameData(
            myBoard = myBoard,
            myShotsBoard = myShotsBoard,
            shipsToPlace = shipsToPlace,
            currentPlayerIndex = jsonObject.optInt("currentPlayerIndex", 0),
            isTurn = jsonObject.optBoolean("isTurn", true),
            gameState = jsonObject.optString("gameState", "initial"),
            placeShipsFlag = jsonObject.optInt("placeShipsFlag", 1),
            shipsPlacedCount = jsonObject.optInt("shipsPlacedCount", 0),
            missilesFiredCount = jsonObject.optInt("missilesFiredCount", 0)
        )
    }

    fun updateGameState(newState: String) {
        val gameData = loadGame()
        gameData.gameState = newState
        saveGame(gameData)
    }

    fun isTurn(): Boolean {
        return loadGame().isTurn
    }

    fun getNextShipOrientation(): Boolean {
        val gameData = loadGame()
        return if (gameData.shipsToPlace.isNotEmpty()) {
            gameData.shipsToPlace.first().isHorizontal
        } else {
            // Default value or error handling
            false
        }
    }

    fun placeShip(row: Int, col: Int, length: Int, isHorizontal: Boolean) {
        val gameData = loadGame()
        if (isHorizontal) {
            for (i in 0 until length) {
                gameData.myBoard[row][col + i] = 2
            }
        } else {
            for (i in 0 until length) {
                gameData.myBoard[row + i][col] = 2
            }
        }
        gameData.shipsToPlace = gameData.shipsToPlace.drop(1)
        gameData.shipsPlacedCount++
        if (gameData.shipsPlacedCount >= 3) {
            gameData.placeShipsFlag = 0 // Disable ship placement
        }
        saveGame(gameData)
    }

    fun fireMissile(row: Int, col: Int): String {
        val gameData = loadGame()
        return if (gameData.myShotsBoard[row][col] == 0) {
            gameData.myShotsBoard[row][col] = 1
            saveGame(gameData)

            // Send the FIRE_MISSILE action to the server
            val message = JSONObject().apply {
                put("action", "FIRE_MISSILE")
                put("row", row)
                put("col", col)
                put("gameData", gameDataToJson(gameData)) // Include the updated game data
            }
            webSocketClient.send(message.toString())

            "Missile fired at ($row, $col)"
        } else {
            "Already fired at ($row, $col)"
        }
    }

    private fun gameDataToJson(gameData: GameData): String {
        val jsonObject = JSONObject().apply {
            put("myBoard", JSONArray(gameData.myBoard.map { JSONArray(it.toList()) }))
            put("myShotsBoard", JSONArray(gameData.myShotsBoard.map { JSONArray(it.toList()) }))
            put("shipsToPlace", JSONArray(gameData.shipsToPlace.map { ship ->
                JSONObject().apply {
                    put("length", ship.length)
                    put("isHorizontal", ship.isHorizontal)
                }
            }))
            put("currentPlayerIndex", gameData.currentPlayerIndex)
            put("isTurn", gameData.isTurn)
            put("gameState", gameData.gameState)
            put("placeShipsFlag", gameData.placeShipsFlag)
            put("shipsPlacedCount", gameData.shipsPlacedCount)
            put("missilesFiredCount", gameData.missilesFiredCount)
        }

        // Limpiar el contenido del archivo antes de escribir
        val file = File(context.filesDir, "gameData.json")
        file.writeText("") // Limpiar el contenido del archivo
        file.writeText(jsonObject.toString())

        return jsonObject.toString()
    }
}