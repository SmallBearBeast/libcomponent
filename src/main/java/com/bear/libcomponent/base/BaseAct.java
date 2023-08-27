package com.bear.libcomponent.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Request Permission可以使用PermissionX库来代替。
 * implementation 'com.guolindev.permissionx:permissionx:1.7.1'
 * StartActivityResult 可以用androidx的ActivityResultLauncher来代替。
 */
public abstract class BaseAct extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

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

    /**
     * This method is called when clicking the back button.
     */
    public void addBackPressedListener(@NonNull BackPressedHelper.BackPressedListener listener) {
        BackPressedHelper.addBackPressedListener(this, listener);
    }

    public View getDecorView() {
        return getWindow().getDecorView();
    }

    protected abstract int layoutId();
}
