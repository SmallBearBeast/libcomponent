package com.bear.libcomponent.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bear.libcomponent.R;

public abstract class BaseFrag extends Fragment {
    protected String TAG = getClass().getSimpleName();
    private boolean firstVisible = true;
    private BaseAct baseAct;
    private BaseFrag baseFrag;

    private Toolbar toolbar;

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getContext() instanceof BaseAct) {
            baseAct = (BaseAct) getContext();
            Intent intent = baseAct.getIntent();
            if (intent != null) {
                handleIntent(intent);
            }
        }
        if (getParentFragment() instanceof BaseFrag) {
            baseFrag = (BaseFrag) getParentFragment();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            handleArgument(bundle);
        }
    }

    protected void handleIntent(@NonNull Intent intent) {

    }

    protected void handleArgument(@NonNull Bundle bundle) {

    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutView();
        if (view == null && layoutId() != 0) {
            view = inflater.inflate(layoutId(), container, false);
        }
        if (view != null) {
            toolbar = view.findViewById(R.id.lib_base_toolbar_id);
            if (toolbar != null) {
                // 若没有则，setSupportActionbar，则onCreateOptionsMenu不会回调。
                // 若调用setSupportActionBar，则在onCreateOptionsMenu(Menu menu)中获取menu引用，否则直接Menu menu = mToolbar.getMenu();
                getBaseAct().setSupportActionBar(toolbar);
                // 添加Options Menu时，需要额外调用setHasOptionsMenu(true);方法，确保onCreateOptionsMenu()方法得以调用。
                setHasOptionsMenu(true);
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatchFirstVisible();
    }

    private void dispatchFirstVisible() {
        if (firstVisible) {
            onFirstVisible();
            firstVisible = false;
        }
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        baseAct = null;
        baseFrag = null;
    }

    /**
     * This method is called when clicking the back button.
     */
    public void addBackPressedListener(@NonNull BackPressedHelper.BackPressedListener listener) {
        BackPressedHelper.addBackPressedListener(this, listener);
    }

    /**
     * This method is called when fragment is first visible
     */
    protected void onFirstVisible() {

    }

    protected abstract int layoutId();

    protected View layoutView() {
        return null;
    }

    public BaseAct getBaseAct() {
        return baseAct;
    }

    public BaseFrag getBaseFrag() {
        return baseFrag;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
