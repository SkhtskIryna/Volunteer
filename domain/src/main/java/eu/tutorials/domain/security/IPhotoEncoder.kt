package eu.tutorials.domain.security

interface IPhotoEncoder {
    fun encode(bytes: ByteArray): String
    fun decode(base64: String): ByteArray
}