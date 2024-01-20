package com.example.movieapp.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PermissionManager: BasePermissionManager() {
    companion object {
        private const val TAG = "PermissionManager"

        /**
         * A static method to request permission from activity.
         *
         * @param activity an instance of [AppCompatActivity]
         * @param requestId Request ID for permission request
         * @param permissions Permission(s) to request
         *
         * @return [PermissionResult]
         *
         * Suspends the coroutines until result is available.
         */
        suspend fun requestPermissions(activity: AppCompatActivity, requestId: Int, vararg permissions: String): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext requestPermissions(activity, requestId, *permissions)
            }
        }

        suspend fun requestPermissions(activityOrFragment: Any, requestId: Int, vararg permissions: String): PermissionResult {
            val fragmentManager = if (activityOrFragment is AppCompatActivity) {
                activityOrFragment.supportFragmentManager
            } else {
                (activityOrFragment as Fragment).childFragmentManager
            }
            return if (fragmentManager.findFragmentByTag(TAG) != null) {
                val permissionManager = fragmentManager.findFragmentByTag(TAG) as PermissionManager
                permissionManager.completableDeferred = CompletableDeferred()
                permissionManager.requestPermissions(requestId, *permissions)
                permissionManager.completableDeferred.await()
            } else {
                val permissionManager = PermissionManager().apply {
                    if(::completableDeferred.isInitialized && completableDeferred.isActive){
                        completableDeferred.cancel()
                    }
                    completableDeferred = CompletableDeferred()
                }
                fragmentManager.beginTransaction().add(permissionManager, TAG).commitNowAllowingStateLoss()
                permissionManager.requestPermissions(requestId, *permissions)
                permissionManager.completableDeferred.await()
            }
        }

        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
        }
    }

    private lateinit var completableDeferred: CompletableDeferred<PermissionResult>

    override fun onPermissionResult(permissionResult: PermissionResult) {
        // When fragment gets recreated due to memory constraints by OS completableDeferred would be
        // uninitialized and hence check
        if (::completableDeferred.isInitialized) {
            completableDeferred.complete(permissionResult)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::completableDeferred.isInitialized && completableDeferred.isActive) {
            completableDeferred.cancel()
        }
    }
}