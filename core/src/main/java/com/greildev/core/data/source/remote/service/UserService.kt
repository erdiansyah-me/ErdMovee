package com.greildev.core.data.source.remote.service

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.utils.SourceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class UserService @Inject constructor(
    private val firebaseAuthService: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage
) {
    suspend fun userData(): Flow<FirebaseUser?> = flow {
        val user = firebaseAuthService.currentUser
        if (user != null) {
            emit(user)
        } else {
            emit(null)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun loginUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> {
        return callbackFlow {
            firebaseAuthService.signInWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).addOnCompleteListener {
                trySend(SourceResult.Success(it.isSuccessful))
            }.addOnFailureListener {
                trySend(SourceResult.Error(123, it.message ?: "Something Went Wrong!"))
            }
            awaitClose()
        }
    }

    suspend fun registerUser(authRequest: AuthRequest): Flow<SourceResult<Boolean>> =
        callbackFlow<SourceResult<Boolean>> {
            firebaseAuthService.createUserWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).addOnCompleteListener {
                trySend(SourceResult.Success(it.isSuccessful))
            }.addOnFailureListener {
                trySend(SourceResult.Error(123, it.message ?: "Something Went Wrong!"))
            }
            awaitClose()
        }.flowOn(Dispatchers.IO)


    fun updateProfile(profile: ProfileRequest): Flow<SourceResult<String>> =
        callbackFlow<SourceResult<String>> {
            val user = firebaseAuthService.currentUser
            if (user != null) {
                val profileUpdates = if (profile.photo != null) {
                    val photoUriStorage = uploadToFirebaseStorage(user, profile.photo)
                    userProfileChangeRequest {
                        displayName = profile.username
                        photoUri = photoUriStorage
                    }
                } else {
                    userProfileChangeRequest {
                        displayName = profile.username
                    }
                }
                user.updateProfile(profileUpdates)
                    .addOnSuccessListener {
                        user.displayName?.let { it1 ->
                            trySend(SourceResult.Success(it1))
                        }
                    }.addOnFailureListener {
                        trySend(SourceResult.Error(123, it.message ?: "Something Went Wrong!"))
                    }
            } else {
                throw Exception("User not found")
            }
            awaitClose()
        }.flowOn(Dispatchers.IO)

    private fun uploadToFirebaseStorage(user: FirebaseUser, photo: File): Uri {
        val photoName = photo.name
        val storageRef = firebaseStorage.getReference(user.uid + "/profilePictures/" + photoName)
        val task = storageRef.putFile(Uri.parse(photo.path))
        return task.result.storage.downloadUrl.result
    }

    fun logoutUser() {
        firebaseAuthService.signOut()
    }
}