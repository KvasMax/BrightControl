package com.paradise.minimax.brightcontrol.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PermissionManifestFinder {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @NonNull
    public static List<String> findNeededPermissionsFromManifest(Context context) {
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) { /* */ }

        final List<String> needed = new ArrayList<>();
        if (info != null && info.requestedPermissions != null && info.requestedPermissionsFlags != null) {
            for (String permission : info.requestedPermissions) {
                if (PermissionChecker.permissionIsDenied(context, permission)) {
                    needed.add(permission);
                }
            }
        }
        return needed;
    }


}
