package eu.tutorials.domain.security

interface IPasswordHashed {
    fun hash(password: String): String
    fun verify(password: String, hashed: String): Boolean
}
