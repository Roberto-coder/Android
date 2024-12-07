package ipn.mx.batalla_naval_practica5.ui.game

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ipn.mx.batalla_naval_practica5.R

class GameActivity : AppCompatActivity() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var placeShipsGrid: GridLayout
    private lateinit var fireMissilesGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameViewModel = ViewModelProvider(this, GameViewModelFactory(applicationContext)).get(GameViewModel::class.java)

        placeShipsGrid = findViewById(R.id.placeShipsGrid)
        fireMissilesGrid = findViewById(R.id.fireMissilesGrid)

        createGrid(placeShipsGrid, true)
        createGrid(fireMissilesGrid, false)

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            gameViewModel.resetGame()
            resetBoardState()
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            gameViewModel.saveGame()
        }

        drawBoardState()
    }

    private fun createGrid(grid: GridLayout, isShipPlacement: Boolean) {
        grid.removeAllViews()
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val button = Button(this).apply {
                    tag = Pair(row, col)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                    }
                }

                button.setOnClickListener {
                    if (isShipPlacement) {
                        gameViewModel.placeShip(row, col, 3, true)  // Ejemplo: longitud del barco 3, horizontal
                    } else {
                        gameViewModel.fireMissile(row, col)
                    }
                    drawBoardState()
                }

                grid.addView(button)
            }
        }
    }

    private fun drawBoardState() {
        val gameData = gameViewModel.loadGame()
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val button = placeShipsGrid.getChildAt(row * 10 + col) as Button
                when (gameData.board[row][col]) {
                    0 -> button.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    1 -> button.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark))
                    2 -> button.setBackgroundColor(resources.getColor(android.R.color.holo_blue_dark))
                }
            }
        }
    }

    private fun resetBoardState() {
        gameViewModel.resetGame()
        drawBoardState()
    }
}