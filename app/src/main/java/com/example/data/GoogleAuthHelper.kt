package com.example.data

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

sealed class GoogleSignInOutcome {
    data class Success(
        val firebaseUid: String,
        val email: String,
        val displayName: String
    ) : GoogleSignInOutcome()

    data class Cancelled(val message: String) : GoogleSignInOutcome()
    data class Error(val message: String) : GoogleSignInOutcome()
}

object GoogleAuthHelper {
    private const val TAG = "GoogleAuthHelper"

    private fun webClientId(context: Context): String? {
        return runCatching {
            val resId = R.string.default_web_client_id
            context.getString(resId).takeIf { it.isNotBlank() }
        }.getOrNull()
    }

    fun isFirebaseConfigured(context: Context): Boolean = runCatching {
        FirebaseApp.getApps(context).isNotEmpty() || FirebaseApp.initializeApp(context) != null
    }.getOrDefault(false)

    suspend fun signInInteractive(context: Context): GoogleSignInOutcome {
        if (!isFirebaseConfigured(context)) {
            return GoogleSignInOutcome.Error("Firebase ক্লাউড কনফিগার করা যায়নি।")
        }
        val clientId = webClientId(context)
            ?: return GoogleSignInOutcome.Error("Google Web Client ID পাওয়া যায়নি।")

        return try {
            val credentialManager = CredentialManager.create(context)
            val googleOption = GetSignInWithGoogleOption.Builder(clientId).build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCred = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCred).await()
                val user = authResult.user
                    ?: return GoogleSignInOutcome.Error("Firebase ইউজার প্রোফাইল পাওয়া যায়নি।")
                GoogleSignInOutcome.Success(
                    firebaseUid = user.uid,
                    email = user.email.orEmpty(),
                    displayName = user.displayName ?: googleIdTokenCredential.displayName.orEmpty()
                )
            } else {
                GoogleSignInOutcome.Error("অসমর্থিত Google ক্রেডেনশিয়াল ধরন।")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In was cancelled by user or play services config", e)
            GoogleSignInOutcome.Cancelled("Google সাইন-ইন বাতিল করা হয়েছে। পুনরায় চেষ্টা করুন।")
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No Google credentials found on device", e)
            GoogleSignInOutcome.Error("এই ডিভাইসে কোনো Google অ্যাকাউন্ট পাওয়া যায়নি।")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "CredentialManager error during Google Sign-In", e)
            GoogleSignInOutcome.Error("Google সাইন-ইন ত্রুটি: ${e.localizedMessage ?: "অজানা সমস্যা"}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Google Sign-In failure", e)
            GoogleSignInOutcome.Error("ক্লাউড সাইন-ইন ব্যর্থ হয়েছে: ${e.localizedMessage ?: "পুনরায় চেষ্টা করুন"}")
        }
    }

    suspend fun trySilentRestore(context: Context): GoogleSignInOutcome? {
        if (!isFirebaseConfigured(context)) return null
        val existing = runCatching { FirebaseAuth.getInstance().currentUser }.getOrNull()
        if (existing != null) {
            return GoogleSignInOutcome.Success(
                firebaseUid = existing.uid,
                email = existing.email.orEmpty(),
                displayName = existing.displayName.orEmpty()
            )
        }
        val clientId = webClientId(context) ?: return null
        return try {
            val credentialManager = CredentialManager.create(context)
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()
            val response = credentialManager.getCredential(request = request, context = context)
            val cred = response.credential
            if (cred is CustomCredential &&
                cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdCred = GoogleIdTokenCredential.createFrom(cred.data)
                val firebaseCred = GoogleAuthProvider.getCredential(googleIdCred.idToken, null)
                val user = FirebaseAuth.getInstance().signInWithCredential(firebaseCred).await().user
                    ?: return null
                GoogleSignInOutcome.Success(
                    firebaseUid = user.uid,
                    email = user.email.orEmpty(),
                    displayName = user.displayName.orEmpty()
                )
            } else null
        } catch (_: Exception) {
            null
        }
    }

    fun signOutFirebase(context: Context) {
        if (!isFirebaseConfigured(context)) return
        runCatching { FirebaseAuth.getInstance().signOut() }
    }
}
