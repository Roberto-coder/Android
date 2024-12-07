package ipn.mx.batalla_naval_practica5.data.models

data class Board(
    val size: Int = 10, // Tamaño del tablero (por defecto, 10x10)
    val grid: Array<Array<Int>> = Array(size) { Array(size) { 0 } } // Representa el tablero
) {
    // Método para colocar un barco
    fun placeShip(x: Int, y: Int, size: Int, isHorizontal: Boolean): Boolean {
        if (isHorizontal) {
            if (x + size > this.size) return false
            for (i in 0 until size) {
                if (grid[y][x + i] != 0) return false
            }
            for (i in 0 until size) {
                grid[y][x + i] = 1
            }
        } else {
            if (y + size > this.size) return false
            for (i in 0 until size) {
                if (grid[y + i][x] != 0) return false
            }
            for (i in 0 until size) {
                grid[y + i][x] = 1
            }
        }
        return true
    }
}
