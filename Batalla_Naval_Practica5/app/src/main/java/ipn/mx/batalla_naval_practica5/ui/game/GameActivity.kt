package ipn.mx.batalla_naval_practica5.ui.game

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ipn.mx.batalla_naval_practica5.R

class GameActivity : AppCompatActivity() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var myBoardGrid: GridLayout
    private lateinit var myShotsGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameViewModel = ViewModelProvider(this, GameViewModelFactory(applicationContext)).get(GameViewModel::class.java)

        myBoardGrid = findViewById(R.id.myBoardGrid)
        myShotsGrid = findViewById(R.id.myShotsGrid)

        createGrid(myBoardGrid, true)
        createGrid(myShotsGrid, false)

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
                val imageView = ImageView(this).apply {
                    tag = Pair(row, col)
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(col, 1f)
                        rowSpec = GridLayout.spec(row, 1f)
                    }
                    setBackgroundResource(R.drawable.cell_background)
                }

                imageView.setOnClickListener {
                    val message = if (isShipPlacement) {
                        placeShip(row, col, 3, gameViewModel.getNextShipOrientation())
                    } else {
                        gameViewModel.fireMissile(row, col)
                    }
                    Toast.makeText(this@GameActivity, message, Toast.LENGTH_SHORT).show()
                    drawBoardState()
                }

                grid.addView(imageView)
            }
        }
    }

    private fun placeShip(row: Int, col: Int, length: Int, isHorizontal: Boolean): String {
        if (!gameViewModel.canPlaceMoreShips()) {
            return "Ya has colocado todos los barcos"
        }

        if (!gameViewModel.isValidPlacement(row, col, length, isHorizontal)) {
            return "No hay suficiente espacio para colocar el barco en esa posición"
        }

        val grid = myBoardGrid
        val imageView = ImageView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = if (isHorizontal) GridLayout.spec(col, length, 1f) else GridLayout.spec(col, 1f)
                rowSpec = if (isHorizontal) GridLayout.spec(row, 1f) else GridLayout.spec(row, length, 1f)
            }
            setImageResource(if (isHorizontal) R.drawable.barcohorizontal else R.drawable.barcovertical)
        }
        grid.addView(imageView)
        gameViewModel.placeShip(row, col, length, isHorizontal) // Update game state
        return "Ship placed at ($row, $col)"
    }

    private fun drawBoardState() {
        val gameData = gameViewModel.loadGame()
        drawGridState(myBoardGrid, gameData.myBoard)
        drawGridState(myShotsGrid, gameData.myShotsBoard)
    }

    private fun drawGridState(grid: GridLayout, board: Array<Array<Int>>) {
        for (row in 0 until 10) {
            for (col in 0 until 10) {
                val imageView = grid.getChildAt(row * 10 + col) as ImageView
                when (board[row][col]) {
                    0 -> imageView.setImageResource(0) // No image
                    1 -> imageView.setImageResource(R.drawable.misil)
                    2 -> imageView.setImageResource(R.drawable.barcohorizontal)
                }
            }
        }
    }

    private fun resetBoardState() {
        gameViewModel.resetGame()
        createGrid(myBoardGrid, true)
        createGrid(myShotsGrid, false)
    }
}