package com.paradise.minimax.brightcontrol.utils;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

public class PermissionChecker {

    public static boolean permissionIsDenied(Context context, String permission) {
        if (PermissionChecker.isExceptionalPermission(permission)) {
            if (PermissionChecker.exceptionalPermissionIsDenied(context, permission)) {
                return true;
            }
        } else {
            if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExceptionalPermission(String permission) {
        if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            return true;
        } else if (permission.equals(Manifest.permission.PACKAGE_USAGE_STATS)) {
            return true;
        } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
            return true;
        } else if (permission.equals(Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            return true;
        }
        return false;
    }

    private static boolean exceptionalPermissionIsDenied(Context context, String permission) {
        if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(context)) {
            return true;

        } else if (permission.equals(Manifest.permission.PACKAGE_USAGE_STATS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());

            if (mode == AppOpsManager.MODE_DEFAULT) {
                return context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_DENIED;
            } else {
                return mode != AppOpsManager.MODE_ALLOWED;
            }

        } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !Settings.System.canWrite(context);
        } else if (permission.equals(Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
            return false;
        }
        return false;
    }

    public static String getActionForExceptionalPermission(String permission) {
        if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            return Settings.ACTION_MANAGE_OVERLAY_PERMISSION;
        } else if (permission.equals(Manifest.permission.PACKAGE_USAGE_STATS)) {
            return Settings.ACTION_USAGE_ACCESS_SETTINGS;
        } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
            return Settings.ACTION_MANAGE_WRITE_SETTINGS;
        }
        return "";
    }

}
