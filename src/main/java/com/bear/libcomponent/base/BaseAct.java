package com.bear.libcomponent.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAct extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    private static final int Permission_Request_Code = 1;
    private PermissionListener permissionListener;
    private ActResultListener actResultListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        }
        setContentView(layoutId());
    }

    protected void handleIntent(@NonNull Intent intent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (actResultListener != null) {
            actResultListener.onActivityResult(requestCode, resultCode, data);
            actResultListener = null;
        }
    }

    /**
     * Request permission
     *
     * @param permissions The requested permissions.
     * @param listener    The result listener of requested permission.
     * @return true: Do request permission. false: Have request permission and not to do.
     */
    public boolean requestPermissions(String[] permissions, PermissionListener listener) {
        permissionListener = listener;
        List<String> needToAsk = new ArrayList<>();
        for (String s : permissions) {
            if (!isCheckPermission(s)) {
                needToAsk.add(s);
            } else {
                if (isIgnorePermission(s)) {
                    needToAsk.add(s);
                }
            }
        }
        if (!needToAsk.isEmpty()) {
            ActivityCompat.requestPermissions(this, needToAsk.toArray(new String[needToAsk.size()]), Permission_Request_Code);
            return true;
        }
        permissionListener = null;
        return false;
    }

    public boolean isIgnorePermission(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    public boolean isCheckPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission_Request_Code) {
            List<String> permissionSuccessArray = new ArrayList<>();
            List<String> permissionFailArray = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionSuccessArray.add(permissions[i]);
                } else {
                    permissionFailArray.add(permissions[i]);
                }
            }
            if (permissionListener != null) {
                permissionListener.onPermissionRequest(permissionSuccessArray, permissionFailArray);
                permissionListener = null;
            }
        }
    }

    public void addBackPressedListener(@NonNull BackPressedHelper.BackPressedListener listener) {
        BackPressedHelper.addBackPressedListener(this, listener);
    }

    protected void setActResultListener(ActResultListener actResultListener) {
        this.actResultListener = actResultListener;
    }

    public View getDecorView() {
        return getWindow().getDecorView();
    }

    protected abstract int layoutId();

    public interface ActResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface PermissionListener {
        void onPermissionRequest(List<String> permissionSuccessArray, List<String> permissionFailArray);
    }
}
