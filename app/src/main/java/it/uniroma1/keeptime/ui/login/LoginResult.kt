package it.uniroma1.keeptime.ui.login

import it.uniroma1.keeptime.data.model.Worker

/**
 * Authentication result: success (user details) or error message.
 */
data class LoginResult(val success: Worker? = null, val error: Int? = null)
