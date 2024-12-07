package ipn.mx.batalla_naval_practica5.data.models

data class GameData(
    val board: Array<Array<Int>>,
    val players: List<Player>,
    val currentPlayerIndex: Int
)