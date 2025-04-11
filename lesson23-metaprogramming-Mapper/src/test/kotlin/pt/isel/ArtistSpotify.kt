package pt.isel

class ArtistSpotify(
    @property:Match(name = "country") val state: State,
    val kind: String,
    val name: String,
)