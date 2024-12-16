package ipn.mx.batalla_naval_practica5.ui.game

import android.content.Context
import androidx.lifecycle.ViewModel
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository

class GameViewModel(private val context: Context) : ViewModel() {
    private val gameRepository = GameRepository(context)

    fun placeShip(row: Int, col: Int, length: Int, isHorizontal: Boolean): String {
        val gameData = gameRepository.loadGame()

        if (gameData.shipsToPlace.isEmpty()) {
            return "Ya has colocado todos los barcos"
        }

        if (!isValidPlacement(gameData.myBoard, row, col, length, isHorizontal)) {
            return "No hay suficiente espacio para colocar el barco en esa posición"
        }

        for (i in 0 until length) {
            if (isHorizontal) {
                gameData.myBoard[row][col + i] = 2  // 2 para marcar el barco
            } else {
                gameData.myBoard[row + i][col] = 2
            }
        }

        gameData.shipsToPlace = gameData.shipsToPlace.drop(1)
        gameRepository.saveAsJson(gameData)
        return "¡Barco colocado con éxito en la posición ($row, $col)"
    }

    fun fireMissile(row: Int, col: Int): String {
        val gameData = gameRepository.loadGame()

        if (!gameData.isTurn) {
            return "No es tu turno"
        }

        if (gameData.myShotsBoard[row][col] != 0) {
            return "Ya has disparado en esta posición"
        }

        gameData.myShotsBoard[row][col] = 1  // 1 para marcar un disparo
        gameData.isTurn = false
        gameRepository.saveAsJson(gameData)
        return "Disparo realizado en la posición ($row, $col)"
    }

    fun loadGame(): GameData {
        return gameRepository.loadGame()
    }

    fun saveGame() {
        val gameData = gameRepository.loadGame()
        gameRepository.saveAsJson(gameData)
    }

    fun resetGame() {
        gameRepository.resetGame()
    }

    fun canPlaceMoreShips(): Boolean {
        val gameData = gameRepository.loadGame()
        return gameData.shipsToPlace.isNotEmpty()
    }

    fun isValidPlacement(row: Int, col: Int, length: Int, isHorizontal: Boolean): Boolean {
        val gameData = gameRepository.loadGame()
        return isValidPlacement(gameData.myBoard, row, col, length, isHorizontal)
    }

    fun getNextShipOrientation(): Boolean {
        val gameData = gameRepository.loadGame()
        return when (gameData.shipsToPlace.size) {
            3, 2 -> false // Vertical for the first two ships
            1 -> true // Horizontal for the last ship
            else -> true
        }
    }

    private fun isValidPlacement(board: Array<Array<Int>>, x: Int, y: Int, shipLength: Int, isHorizontal: Boolean): Boolean {
        if (isHorizontal) {
            if (y + shipLength > board[0].size) return false
            for (i in 0 until shipLength) {
                if (board[x][y + i] != 0) return false
            }
        } else {
            if (x + shipLength > board.size) return false
            for (i in 0 until shipLength) {
                if (board[x + i][y] != 0) return false
            }
        }
        return true
    }
}