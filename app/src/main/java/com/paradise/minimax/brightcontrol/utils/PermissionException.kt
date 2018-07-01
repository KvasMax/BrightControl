package com.paradise.minimax.brightcontrol.utils

import java.lang.Exception

class PermissionException(val permissionResult: PermissionResult) : Exception() {

    val accepted: List<String>
    val foreverDenied: List<String>
    val denied: List<String>
    val runtimePermission: RuntimePermission

    init {
        accepted = permissionResult.accepted
        foreverDenied = permissionResult.foreverDenied
        denied = permissionResult.denied
        runtimePermission = permissionResult.runtimePermission
    }

    fun goToSettings() {
        permissionResult.goToSettings()
    }

    fun isAccepted(): Boolean {
        return permissionResult.isAccepted
    }

    fun hasDenied(): Boolean {
        return permissionResult.hasDenied()
    }

    fun hasForeverDenied(): Boolean {
        return permissionResult.hasForeverDenied()
    }

}