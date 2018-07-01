package com.paradise.minimax.brightcontrol.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * DO NOT USE THIS FRAGMENT DIRECTLY!
 * It's only here because fragments have to be public
 */
public class PermissionFragment extends Fragment {

    public static final String LIST_PERMISSIONS = "LIST_PERMISSIONS";

    private static final int REQUEST_CODE = 23;
    private final List<String> acceptedPermissions = new ArrayList<>();
    private final List<String> askAgainPermissions = new ArrayList<>();
    private final List<String> refusedPermissions = new ArrayList<>();
    private List<String> permissionsList = new ArrayList<>();
    private String requestedPermission;

    @Nullable
    private WeakReference<PermissionListener> listener;

    public PermissionFragment() {
        setRetainInstance(true);
    }

    public static PermissionFragment newInstance(List<String> permissions) {
        final Bundle args = new Bundle();
        args.putStringArrayList(LIST_PERMISSIONS, new ArrayList<>(permissions));
        final PermissionFragment fragment = new PermissionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            final List<String> permissionsArgs = arguments.getStringArrayList(LIST_PERMISSIONS);
            if (permissionsArgs != null) {
                permissionsList.addAll(permissionsArgs);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!permissionsList.isEmpty()) {
            processPermissions();
        } else {
            // this shouldn't happen, but just to be sure
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && this.listener != null) {
            processPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE && this.listener != null) {
            processPermissions();
        }
    }

    private void processPermissions() {
        if (requestedPermission != null) {
            if (PermissionChecker.permissionIsDenied(getContext(), requestedPermission)) {
                if (PermissionChecker.isExceptionalPermission(requestedPermission)) {
                    refusedPermissions.add(requestedPermission);
                } else {
                    if (shouldShowRequestPermissionRationale(requestedPermission)) {
                        askAgainPermissions.add(requestedPermission);
                    } else {
                        refusedPermissions.add(requestedPermission);
                    }
                }

            } else {
                acceptedPermissions.add(requestedPermission);
            }
            permissionsList.remove(requestedPermission);
            requestedPermission = null;
        }
        if (permissionsList.isEmpty()) {
            final PermissionListener listener = this.listener.get();
            if (listener != null) {
                listener.onRequestPermissionsResult(acceptedPermissions, refusedPermissions, askAgainPermissions);
            }
            getFragmentManager().beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        } else {
            requestedPermission = permissionsList.get(0);
            if (PermissionChecker.isExceptionalPermission(requestedPermission)) {
                startActivityForResult(
                        new Intent(PermissionChecker.getActionForExceptionalPermission(requestedPermission)),
                        REQUEST_CODE);
            } else {
                requestPermissions(new String[]{requestedPermission}, REQUEST_CODE);
            }
        }
    }

    public PermissionFragment setListener(@Nullable PermissionListener listener) {
        if (listener != null) {
            if (this.listener != null) {
                this.listener.clear();
            }

            this.listener = new WeakReference<>(listener);
        }
        return this;
    }

    interface PermissionListener {
        void onRequestPermissionsResult(List<String> acceptedPermissions,
                                        List<String> refusedPermissions,
                                        List<String> askAgainPermissions);
    }
}
