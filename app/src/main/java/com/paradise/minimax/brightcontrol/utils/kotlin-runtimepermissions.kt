package com.paradise.minimax.brightcontrol.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

fun Fragment.askPermission(vararg permissions: String,
                           acceptedBlock: ((PermissionResult) -> Unit)? = null,
                           deniedBlock: ((PermissionResult) -> Unit)? = null,
                           foreverDeniedBlock: ((PermissionResult) -> Unit)? = null) {

    RuntimePermission.newInstance(activity)
            .setPermissions(permissions.toList())
            .onAccepted(acceptedBlock)
            .onDenied(deniedBlock)
            .onForeverDenied(foreverDeniedBlock)
            .ask()
}

fun FragmentActivity.askPermission(vararg permissions: String,
                                   acceptedBlock: ((PermissionResult) -> Unit)? = null,
                                   deniedBlock: ((PermissionResult) -> Unit)? = null,
                                   foreverDeniedBlock: ((PermissionResult) -> Unit)? = null) {

    RuntimePermission.newInstance(this)
            .setPermissions(permissions.toList())
            .onAccepted(acceptedBlock)
            .onDenied(deniedBlock)
            .onForeverDenied(foreverDeniedBlock)
            .ask()
}

fun FragmentActivity.shouldAskPermissions(vararg permissions: String): Boolean {
    return RuntimePermission.newInstance(this)
            .setPermissions(permissions.toList())
            .shouldAskPermissions()
}