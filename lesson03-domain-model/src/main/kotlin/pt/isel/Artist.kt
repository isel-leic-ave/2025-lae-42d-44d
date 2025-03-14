package pt.isel

/**
 * Immutable properties
 * DOES not provide a parameterless constructor
 */
class Artist(
    val kind: String,
    val name: String,
    val country: Country,
)
