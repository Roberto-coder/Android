package ipn.mx.batalla_naval_practica5.data.models

data class GameData(
    val myBoard: Array<Array<Int>>,
    val myShotsBoard: Array<Array<Int>>,
    var shipsToPlace: List<Ship>,
    val currentPlayerIndex: Int,
    var isTurn: Boolean,
    var gameState: String, // Nueva bandera para el estado del juego
    var placeShipsFlag: Int = 1, // Bandera para habilitar/deshabilitar la colocación de barcos
    var shipsPlacedCount: Int = 0, // Contador de barcos colocados
    var missilesFiredCount: Int = 0 // Contador de misiles lanzados
)

data class Ship(
    val length: Int,
    val isHorizontal: Boolean
)