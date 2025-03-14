package pt.isel

class Artist(
    val kind: String,
    val name: String,
    val country: Country = Country("PT", "Portuguese"),
)
