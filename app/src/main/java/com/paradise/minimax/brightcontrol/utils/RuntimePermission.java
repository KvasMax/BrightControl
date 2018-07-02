package com.paradise.minimax.brightcontrol.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.paradise.minimax.brightcontrol.utils.callbacks.AcceptedCallback;
import com.paradise.minimax.brightcontrol.utils.callbacks.DeniedCallback;
import com.paradise.minimax.brightcontrol.utils.callbacks.ForeverDeniedCallback;
import com.paradise.minimax.brightcontrol.utils.callbacks.PermissionListener;
import com.paradise.minimax.brightcontrol.utils.callbacks.ResponseCallback;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuntimePermission {

    private final Reference<FragmentActivity> activityReference;

    private final List<String> permissionsToRequest = new ArrayList<>();

    private final List<ResponseCallback> responseCallbacks = new ArrayList<>();
    private final List<AcceptedCallback> acceptedCallbacks = new ArrayList<>();
    private final List<ForeverDeniedCallback> foreverDeniedCallbacks = new ArrayList<>();
    private final List<DeniedCallback> deniedCallbacks = new ArrayList<>();
    private final List<PermissionListener> permissionListeners = new ArrayList<>();

    private final PermissionFragment.PermissionListener listener = new PermissionFragment.PermissionListener() {
        @Override
        public void onRequestPermissionsResult(List<String> acceptedPermissions, List<String> refusedPermissions, List<String> askAgainPermissions) {
            onReceivedPermissionResult(acceptedPermissions, refusedPermissions, askAgainPermissions);
        }
    };

    public RuntimePermission(@Nullable final FragmentActivity activity) {
        if (activity != null) {
            this.activityReference = new WeakReference<>(activity);
        } else {
            this.activityReference = new WeakReference<>(null);
        }
    }

    /**
     * Fill permissions to only ask If we do not call this method,
     * If not set or empty, the library will find all needed permissions to ask from manifest
     * You can call .setPermissions(permissions) after this method if you want to give permissions in a separate method
     */
    public static RuntimePermission newInstance(@Nullable final FragmentActivity activity, String... permissions) {
        return new RuntimePermission(activity).setPermissions(permissions);
    }

    /**
     * Fill permissions to only ask If we do not call this method,
     * If not set or empty, the library will find all needed permissions to ask from manifest
     * You can call .setPermissions(permissions) after this method if you want to give permissions in a separate method
     */
    public static RuntimePermission newInstance(@Nullable final Fragment fragment, String... permissions) {
        @Nullable FragmentActivity activity = null;
        if (fragment != null) {
            activity = fragment.getActivity();
        }
        return newInstance(activity).setPermissions(permissions);
    }

    /**
     * Just a helper methods in case the user blocks permission.
     * It goes to your application settings page for the user to enable permission again.
     */
    public void goToSettings() {
        final FragmentActivity fragmentActivity = this.activityReference.get();
        if (fragmentActivity != null) {
            fragmentActivity.startActivity(new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", fragmentActivity.getPackageName(), null)));
        }
    }

    private void onReceivedPermissionResult(List<String> acceptedPermissions,
                                            List<String> refusedPermissions,
                                            List<String> askAgainPermissions) {

        final PermissionResult permissionResult = new PermissionResult(this, acceptedPermissions, refusedPermissions, askAgainPermissions);
        if (permissionResult.isAccepted()) {
            for (AcceptedCallback callback : acceptedCallbacks) {
                callback.onAccepted(permissionResult);
            }
            for (PermissionListener permissionListener : permissionListeners) {
                permissionListener.onAccepted(this, permissionResult.getAccepted());
            }
        }
        if (permissionResult.hasDenied()) {
            for (DeniedCallback callback : deniedCallbacks) {
                callback.onDenied(permissionResult);
            }
        }
        if (permissionResult.hasForeverDenied()) {
            for (ForeverDeniedCallback callback : foreverDeniedCallbacks) {
                callback.onForeverDenied(permissionResult);
            }
        }

        if (permissionResult.hasForeverDenied() || permissionResult.hasDenied()) {
            for (PermissionListener permissionListener : permissionListeners) {
                permissionListener.onDenied(this, permissionResult.getDenied(), permissionResult.getForeverDenied());
            }
        }

        for (ResponseCallback responseCallback : responseCallbacks) {
            responseCallback.onResponse(permissionResult);
        }
    }

    /**
     * We want to only setPermissions given permissions
     * If we do not call this method, the library will find all needed permissions to ask from manifest
     *
     * @see android.Manifest.permission
     */
    public RuntimePermission setPermissions(@Nullable final List<String> permissions) {
        if (permissions != null) {
            permissionsToRequest.clear();
            permissionsToRequest.addAll(permissions);
        }
        return this;
    }

    /**
     * We want to only setPermissions given permissions
     *
     * @see android.Manifest.permission
     */
    public RuntimePermission setPermissions(@Nullable final String... permissions) {
        if (permissions != null) {
            return this.setPermissions(Arrays.asList(permissions));
        } else {
            return this;
        }
    }

    public RuntimePermission onResponse(@Nullable final ResponseCallback callback) {
        if (callback != null) {
            responseCallbacks.add(callback);
        }
        return this;
    }

    public RuntimePermission onResponse(@Nullable final PermissionListener permissionListener) {
        if (permissionListener != null) {
            permissionListeners.add(permissionListener);
        }
        return this;
    }

    public RuntimePermission onAccepted(@Nullable final AcceptedCallback callback) {
        if (callback != null) {
            acceptedCallbacks.add(callback);
        }
        return this;
    }

    public RuntimePermission onDenied(@Nullable final DeniedCallback callback) {
        if (callback != null) {
            deniedCallbacks.add(callback);
        }
        return this;
    }

    public RuntimePermission onForeverDenied(@Nullable final ForeverDeniedCallback callback) {
        if (callback != null) {
            foreverDeniedCallbacks.add(callback);
        }
        return this;
    }

    public boolean shouldAskPermissions() {
        final FragmentActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) {
            return false;
        }

        final List<String> permissions = findNeededPermissions(activity);

        if (permissions.isEmpty() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M || arePermissionsAlreadyAccepted(activity, permissions)) {
            return false;
        } else {
            return true;
        }
    }

    public void ask(@Nullable ResponseCallback responseCallback) {
        onResponse(responseCallback)
                .ask();
    }

    public void ask(@Nullable PermissionListener permissionListener) {
        onResponse(permissionListener)
                .ask();
    }

    /**
     * If we setPermissions permission using .setPermissions(names), we only ask them
     * If not, this lib will search needed permissions from Manifest
     */
    private List<String> findNeededPermissions(@NonNull Context context) {
        if (permissionsToRequest.isEmpty()) {
            return PermissionManifestFinder.findNeededPermissionsFromManifest(context);
        } else {
            return permissionsToRequest;
        }
    }

    /**
     * Ask for the permission. Which permission? Anything you register on your manifest that needs it.
     * It is safe to call this every time without querying `shouldAsk`.
     * In case you call `ask` without needing any permission, bitteBitte will immediately receive `yesYouCan()`
     */
    public void ask() {
        final FragmentActivity activity = activityReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        //retrieve permissions we want
        final List<String> permissions = findNeededPermissions(activity);

        //ne need < Android Marshmallow
        if (permissions.isEmpty() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M || arePermissionsAlreadyAccepted(activity, permissions)) {
            onAllAccepted(permissions);
        } else {
            final PermissionFragment oldFragment = (PermissionFragment) activity
                    .getSupportFragmentManager()
                    .findFragmentByTag(PermissionFragment.class.getSimpleName());

            if (oldFragment != null) {
                oldFragment.setListener(listener);
            } else {
                final PermissionFragment newFragment = PermissionFragment.newInstance(permissions);
                newFragment.setListener(listener);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .add(newFragment, PermissionFragment.class.getSimpleName())
                                .commitNowAllowingStateLoss();
                    }
                });

            }
        }
    }

    private boolean arePermissionsAlreadyAccepted(@NonNull Context context, @NonNull final List<String> permissions) {
        for (String permission : permissions) {
            if (PermissionChecker.permissionIsDenied(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private void onAllAccepted(@NonNull final List<String> permissions) {
        onReceivedPermissionResult(permissions, null, null);
    }
}
