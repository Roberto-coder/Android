package ipn.mx.batalla_naval_practica5.domain.usecases

import ipn.mx.batalla_naval_practica5.data.models.Player
import ipn.mx.batalla_naval_practica5.data.repository.GameRepository

class FireMissileUseCase(private val gameRepository: GameRepository) {

    // Ejecutar el caso de uso: disparar un misil en la posición proporcionada
    fun execute(player: Player, x: Int, y: Int): String {
        val gameData = gameRepository.loadGame()

        // Verificar si la posición está dentro de los límites del tablero
        if (x !in gameData.myShotsBoard.indices || y !in gameData.myShotsBoard[x].indices) {
            return "Posición fuera de los límites del tablero"
        }

        // Verificar si el misil ya ha sido disparado en esa posición
        if (gameData.myShotsBoard[x][y] == 1) {
            return "Ya se ha disparado en esta posición"
        }

        // Realizar el disparo: cambiar el estado del tablero
        gameData.myShotsBoard[x][y] = 1  // Se marca la posición con un 1 para indicar que fue impactada

        // Actualizar el estado del juego en el repositorio
        gameRepository.saveAsJson(gameData)

        // Devolver resultado del disparo
        return if (gameData.myBoard[x][y] == 2) {
            "¡Impacto! La posición ($x, $y) ha sido alcanzada"
        } else {
            "El disparo falló"
        }
    }
}