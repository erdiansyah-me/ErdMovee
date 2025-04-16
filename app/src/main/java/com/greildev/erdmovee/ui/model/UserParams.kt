import com.greildev.core.domain.model.UserData

data class UserParams(
    val user: UserData?,
    val isOnboarding: Boolean
)
