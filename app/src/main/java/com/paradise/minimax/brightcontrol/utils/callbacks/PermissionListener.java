package com.paradise.minimax.brightcontrol.utils.callbacks;

import com.paradise.minimax.brightcontrol.utils.RuntimePermission;

import java.util.List;

public interface PermissionListener {
    void onAccepted(RuntimePermission runtimePermission, List<String> accepted);

    void onDenied(RuntimePermission runtimePermission, List<String> denied, List<String> foreverDenied);
}
