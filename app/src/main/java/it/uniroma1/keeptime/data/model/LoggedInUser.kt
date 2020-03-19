package it.uniroma1.keeptime.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
        val authenticationToken: String,  // TODO: secure
        val email: String,
        val url: String
)
