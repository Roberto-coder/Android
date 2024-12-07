package ipn.mx.batalla_naval_practica5.ui.game

import android.content.Context
import androidx.lifecycle.ViewModel
import ipn.mx.batalla_naval_practica5.data.models.GameData
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository

class GameViewModel(private val context: Context) : ViewModel() {
    private val gameRepository = GameRepository(context)

    fun placeShip(row: Int, col: Int, shipLength: Int, isHorizontal: Boolean): String {
        val gameData = gameRepository.loadGame()

        if (!isValidPlacement(gameData.board, row, col, shipLength, isHorizontal)) {
            return "No hay suficiente espacio para colocar el barco en esa posición"
        }

        for (i in 0 until shipLength) {
            if (isHorizontal) {
                gameData.board[row][col + i] = 2  // 2 para marcar el barco
            } else {
                gameData.board[row + i][col] = 2
            }
        }

        gameRepository.saveAsJson(gameData)
        return "¡Barco colocado con éxito en la posición ($row, $col)"
    }

    fun fireMissile(row: Int, col: Int): String {
        val gameData = gameRepository.loadGame()

        if (gameData.board[row][col] == 2) {
            gameData.board[row][col] = 1  // 1 para marcar un impacto
            gameRepository.saveAsJson(gameData)
            return "¡Impacto en la posición ($row, $col)!"
        } else {
            gameData.board[row][col] = 1  // 1 para marcar un disparo fallido
            gameRepository.saveAsJson(gameData)
            return "No hay ningún barco en la posición ($row, $col)"
        }
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