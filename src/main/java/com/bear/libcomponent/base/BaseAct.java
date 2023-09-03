package com.bear.libcomponent.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bear.libcomponent.R;

/**
 * Request Permission可以使用PermissionX库来代替。
 * implementation 'com.guolindev.permissionx:permissionx:1.7.1'
 * StartActivityResult 可以用androidx的ActivityResultLauncher来代替。
 * showProgress和showDialog应该放在专门的util类来实现。
 */
public abstract class BaseAct extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        }
        if (layoutId() == -1) {
            throw new RuntimeException("Must have a layout id when init activity");
        }
        setContentView(layoutId());
        toolbar = findViewById(R.id.lib_base_toolbar_id);
        if (toolbar != null) {
            // 若没有则，setSupportActionbar，则onCreateOptionsMenu不会回调。
            // 若调用setSupportActionBar，则在onCreateOptionsMenu(Menu menu)中获取menu引用，否则直接Menu menu = mToolbar.getMenu();
            setSupportActionBar(toolbar);
        }
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

    public @Nullable Toolbar getToolbar() {
        return toolbar;
    }

    protected abstract int layoutId();
}
