package ipn.mx.wearables.presentation

data class MemeResponse(
    val success: Boolean,
    val data: MemeData
)

data class MemeData(
    val memes: List<Meme>
)