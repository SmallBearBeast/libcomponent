package com.bear.libcomponent.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFrag extends Fragment {
    protected String TAG = getClass().getSimpleName();
    private boolean firstVisible = true;
    private BaseAct baseAct;
    private BaseFrag baseFrag;

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
        if (view != null) {
            return view;
        } else if (layoutId() != 0) {
            return inflater.inflate(layoutId(), container, false);
        }
        return null;
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
}
