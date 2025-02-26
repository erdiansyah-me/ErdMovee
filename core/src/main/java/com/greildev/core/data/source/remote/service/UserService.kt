package com.greildev.core.data.source.remote.service

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.greildev.core.domain.model.AuthRequest
import com.greildev.core.domain.model.ProfileRequest
import com.greildev.core.utils.CoreConstant
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
                if (profile.photo != null) {
                    uploadToFirebaseStorage(user = user, photo = profile.photo,
                        onSuccess = { uri ->
                            val profileUpdates = userProfileChangeRequest {
                                displayName = profile.username
                                photoUri = uri
                            }
                            user.updateProfile(profileUpdates)
                                .addOnSuccessListener {
                                    user.displayName?.let { it1 ->
                                        trySend(SourceResult.Success(it1))
                                    }
                                }.addOnFailureListener {
                                    trySend(
                                        SourceResult.Error(
                                            CoreConstant.ERROR_CODE,
                                            it.message ?: "Something Went Wrong!"
                                        )
                                    )
                                }
                        },
                        onFailure = {
                            trySend(
                                SourceResult.Error(
                                    CoreConstant.ERROR_CODE,
                                    it.localizedMessage ?: "Something Went Wrong!"
                                )
                            )
                        }
                    )
                } else {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = profile.username
                    }
                    user.updateProfile(profileUpdates)
                        .addOnSuccessListener {
                            user.displayName?.let { it1 ->
                                trySend(SourceResult.Success(it1))
                            }
                        }.addOnFailureListener {
                            trySend(
                                SourceResult.Error(
                                    CoreConstant.ERROR_CODE,
                                    it.localizedMessage ?: "Something Went Wrong!"
                                )
                            )
                        }
                }
            } else {
                trySend(SourceResult.Error(CoreConstant.ERROR_CODE, "User Not Found!"))
            }
            awaitClose()
        }.flowOn(Dispatchers.IO)

    private fun uploadToFirebaseStorage(
        user: FirebaseUser,
        photo: File,
        onSuccess: (Uri) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef =
            firebaseStorage.reference.child(user.uid + "/profilePictures/" + Uri.fromFile(photo).lastPathSegment)

        println("storageRef: $storageRef and photo: $photo also ${Uri.fromFile(photo)}")
        val task = storageRef.putFile(Uri.fromFile(photo))
        task.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun logoutUser() {
        firebaseAuthService.signOut()
    }
}