package ipn.mx.batalla_naval_practica5.data.repository

import android.content.Context
import com.google.gson.Gson
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.models.Ship
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class GameRepository(private val context: Context) {

    private val fileName = "gameData.json"

    fun saveAsJson(gameData: GameData) {
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
        val file = File(context.filesDir, "gameData.json")
        file.writeText(jsonObject.toString())
    }

    fun loadGame(): GameData {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return createDefaultGameData()
        }

        val gson = Gson()
        val jsonString = file.readText()
        return gson.fromJson(jsonString, GameData::class.java)
    }

    fun loadGameJson(): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.readText()
        } else {
            "{}" // Return an empty JSON object if the file does not exist
        }
    }

    private fun createDefaultGameData(): GameData {
        return GameData(
            myBoard = Array(10) { Array(10) { 0 } },
            myShotsBoard = Array(10) { Array(10) { 0 } },
            shipsToPlace = listOf(
                Ship(length = 2, isHorizontal = false),
                Ship(length = 3, isHorizontal = false),
                Ship(length = 4, isHorizontal = true)
            ),
            currentPlayerIndex = 0,
            isTurn = true,
            gameState = "initial" // Provide a default value for gameState
        )
    }
}