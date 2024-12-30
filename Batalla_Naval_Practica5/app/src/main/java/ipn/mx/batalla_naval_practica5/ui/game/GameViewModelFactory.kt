package ipn.mx.batalla_naval_practica5.ui.game

import GameWebSocketClient
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private val context: Context,
    private val webSocketClient: GameWebSocketClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(context, webSocketClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}