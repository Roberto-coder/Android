package ipn.mx.batalla_naval_practica5.data.models

data class BoardState(
    val ships: MutableList<Pair<Int, Int>> = mutableListOf(),
    val missiles: MutableList<Pair<Int, Int>> = mutableListOf()
)
