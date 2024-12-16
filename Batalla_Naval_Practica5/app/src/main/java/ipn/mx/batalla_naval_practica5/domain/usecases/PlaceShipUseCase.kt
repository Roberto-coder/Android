package ipn.mx.batalla_naval_practica5.domain.usecases

import ipn.mx.batalla_naval_practica5.data.models.Player
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository

class PlaceShipUseCase(private val gameRepository: GameRepository) {

    // Ejecutar el caso de uso: colocar un barco en las coordenadas proporcionadas
    fun execute(player: Player, shipLength: Int, x: Int, y: Int, isHorizontal: Boolean): String {
        val gameData = gameRepository.loadGame()

        // Verificar si el barco cabe en la posición
        if (!isValidPlacement(gameData.myBoard, x, y, shipLength, isHorizontal)) {
            return "No hay suficiente espacio para colocar el barco en esa posición"
        }

        // Colocar el barco en el tablero
        for (i in 0 until shipLength) {
            if (isHorizontal) {
                gameData.myBoard[x][y + i] = 2  // 2 para marcar el barco
            } else {
                gameData.myBoard[x + i][y] = 2
            }
        }

        // Actualizar el estado del juego en el repositorio
        gameRepository.saveAsJson(gameData)

        // Devolver resultado
        return "¡Barco colocado con éxito en la posición ($x, $y)"
    }

    // Verificar si el barco cabe en el tablero
    private fun isValidPlacement(board: Array<Array<Int>>, x: Int, y: Int, shipLength: Int, isHorizontal: Boolean): Boolean {
        if (isHorizontal) {
            // Verificar si la posición horizontal es válida
            if (y + shipLength > board[0].size) return false
            for (i in 0 until shipLength) {
                if (board[x][y + i] != 0) return false  // 0 significa que la celda está vacía
            }
        } else {
            // Verificar si la posición vertical es válida
            if (x + shipLength > board.size) return false
            for (i in 0 until shipLength) {
                if (board[x + i][y] != 0) return false
            }
        }
        return true
    }
}