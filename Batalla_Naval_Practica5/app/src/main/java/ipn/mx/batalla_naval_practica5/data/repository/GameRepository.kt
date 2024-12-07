package ipn.mx.batalla_naval_practica5.data.repository

import android.content.Context
import com.google.gson.Gson
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.models.Player
import java.io.File

class GameRepository(private val context: Context) {

    private val fileName = "gameData.json"

    fun saveAsJson(gameData: GameData) {
        val gson = Gson()
        val jsonString = gson.toJson(gameData)
        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)
    }

    fun loadGame(): GameData {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return GameData(Array(10) { Array(10) { 0 } }, listOf(Player("Player1", 0, false)), 0)
        }

        val gson = Gson()
        val jsonString = file.readText()
        return gson.fromJson(jsonString, GameData::class.java)
    }

    fun resetGame() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}