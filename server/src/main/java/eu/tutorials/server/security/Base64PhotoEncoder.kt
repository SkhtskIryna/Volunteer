package eu.tutorials.server.security
import eu.tutorials.domain.security.IPhotoEncoder
import java.util.Base64

class Base64PhotoEncoder : IPhotoEncoder {
    override fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    override fun decode(base64: String): ByteArray {
        return Base64.getDecoder().decode(base64)
    }
}
