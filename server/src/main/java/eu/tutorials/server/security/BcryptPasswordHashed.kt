package eu.tutorials.server.security

import eu.tutorials.domain.security.IPasswordHashed
import at.favre.lib.crypto.bcrypt.BCrypt

class BcryptPasswordHashed : IPasswordHashed {
    override fun hash(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    override fun verify(password: String, hash: String): Boolean {
        val result = BCrypt.verifyer().verify(password.toCharArray(), hash)
        return result.verified
    }
}
