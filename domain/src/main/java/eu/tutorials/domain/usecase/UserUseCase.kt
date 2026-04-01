package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Card
import eu.tutorials.domain.model.User
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IUserRepository
import eu.tutorials.domain.security.IPasswordHashed
import eu.tutorials.domain.security.IPhotoEncoder
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import eu.tutorials.domain.utils.compressPhoto

class CreateUserUseCase(
    private val userRepository: IUserRepository,
    private val passwordHasher: IPasswordHashed
) {
    suspend operator fun invoke(user: User, rawPassword: String, card: Card? = null): User {
        val hashedPassword = passwordHasher.hash(rawPassword)

        if (card != null) {
            user.addCard(card)
        }

        val userToSave = user.copy(password = hashedPassword)
        return userRepository.create(userToSave)
    }
}

class DeleteUserUseCase(iUserRepository: IUserRepository): DeleteUseCase<User>(iUserRepository,
    listOf<UserRole>(UserRole.Admin, UserRole.Volunteer, UserRole.Recipient))
class FindUserByIdUseCase(private val userRepository: IUserRepository) {
    suspend operator fun invoke(id: Int): User? = userRepository.findById(id)
}
class GetAllUsersUseCase(private val iUserRepository: IUserRepository) {
    suspend operator fun invoke(): List<User> {
        return iUserRepository.getAll()
    }
}

class UpdateUserUseCase(
    private val userRepository: IUserRepository,
    private val passwordHasher: IPasswordHashed,
    private val photoEncoder: IPhotoEncoder
) {
    suspend operator fun invoke(user: User, rawPassword: String?, rawPhotoBytes: ByteArray?): Boolean {

        val compressedPhotoBase64 = rawPhotoBytes
            ?.let { compressPhoto(it) } // JPEG compressed bytes
            ?.let { photoEncoder.encode(it) } // to Base64

        val updatedUser = user.copy(
            photoBase64 = compressedPhotoBase64 ?: user.photoBase64,
            password = rawPassword?.let { passwordHasher.hash(it) }
        )

        return userRepository.update(updatedUser)
    }
}

class UpdateUserBlockUseCase(
    private val repository: IUserRepository
) {
    suspend operator fun invoke(userId: Int, isBlocked: Boolean): Boolean {
        return repository.updateBlockStatus(userId, isBlocked)
    }
}

class FindUserByEmailUseCase(private val iUserRepository: IUserRepository) {
    suspend operator fun invoke(email: String): User? = iUserRepository.findByEmail(email)
}
class GetPasswordHashUseCase(
    private val userRepository: IUserRepository,
    private val passwordHashed: IPasswordHashed
) {
    suspend operator fun invoke(email: String, password: String): User? {
        val user = userRepository.findByEmail(email) ?: return null
        val isPasswordValid = passwordHashed.verify(password, user.password.toString())
        return if (isPasswordValid) user else null
    }
}
