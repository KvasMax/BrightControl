package com.paradise.minimax.brightcontrol.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

fun Fragment.askPermission(vararg permissions: String, block: ((PermissionResult) -> Unit)? = null): KotlinRuntimePermission {
    return KotlinRuntimePermission(RuntimePermission.askPermission(activity)
            .request(permissions.toList())
            .onResponse(block))
}

fun FragmentActivity.askPermission(vararg permissions: String, block: ((PermissionResult) -> Unit)? = null): KotlinRuntimePermission {
    return KotlinRuntimePermission(RuntimePermission.askPermission(this)
            .request(permissions.toList())
            .onResponse(block))
}

class KotlinRuntimePermission(var runtimePermission: RuntimePermission) {

    init {
        runtimePermission.ask()
    }

    fun onAccepted(block: ((PermissionResult) -> Unit)): KotlinRuntimePermission {
        runtimePermission.onAccepted(block)
        return this
    }

    fun onDenied(block: ((PermissionResult) -> Unit)): KotlinRuntimePermission {
        runtimePermission.onDenied(block)
        return this
    }

    fun onForeverDenied(block: ((PermissionResult) -> Unit)): KotlinRuntimePermission {
        runtimePermission.onForeverDenied(block)
        return this
    }

}
