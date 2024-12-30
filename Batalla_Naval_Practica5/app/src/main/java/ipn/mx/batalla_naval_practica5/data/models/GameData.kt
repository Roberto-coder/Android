package ipn.mx.batalla_naval_practica5.data.models

data class GameData(
    val myBoard: Array<Array<Int>>,
    val myShotsBoard: Array<Array<Int>>,
    var shipsToPlace: List<Ship>,
    val currentPlayerIndex: Int,
    var isTurn: Boolean,
    var gameState: String // Nueva bandera para el estado del juego
)

data class Ship(
    val length: Int,
    val isHorizontal: Boolean
)