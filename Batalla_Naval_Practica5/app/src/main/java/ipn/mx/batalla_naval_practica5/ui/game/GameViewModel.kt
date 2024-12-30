package ipn.mx.batalla_naval_practica5.ui.game

import GameWebSocketClient
import android.content.Context
import androidx.lifecycle.ViewModel
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.models.Ship
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository
import org.json.JSONObject

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
            gameState = jsonObject.optString("gameState", "initial")
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

    fun canPlaceMoreShips(): Boolean {
        return loadGame().shipsToPlace.isNotEmpty()
    }

    fun getNextShipOrientation(): Boolean {
        return loadGame().shipsToPlace.first().isHorizontal
    }

    fun isValidPlacement(row: Int, col: Int, length: Int, isHorizontal: Boolean): Boolean {
        val gameData = loadGame()
        if (isHorizontal) {
            if (col + length > BOARD_SIZE) return false
            for (i in 0 until length) {
                if (gameData.myBoard[row][col + i] != 0) return false
            }
        } else {
            if (row + length > BOARD_SIZE) return false
            for (i in 0 until length) {
                if (gameData.myBoard[row + i][col] != 0) return false
            }
        }
        return true
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
        saveGame(gameData)
    }

    fun fireMissile(row: Int, col: Int): String {
        val gameData = loadGame()
        return if (gameData.myShotsBoard[row][col] == 0) {
            gameData.myShotsBoard[row][col] = 1
            saveGame(gameData)
            "Missile fired at ($row, $col)"
        } else {
            "Already fired at ($row, $col)"
        }
    }
}