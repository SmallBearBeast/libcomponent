package com.bear.libcomponent;

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
import androidx.lifecycle.ViewModelProviders;

public abstract class BaseFrag extends Fragment {
    protected String TAG = getClass().getSimpleName();
    private boolean mIsFirstVisible = true;
    protected BaseAct mBaseAct;
    protected BaseFrag mBaseFrag;

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getContext() instanceof BaseAct) {
            mBaseAct = (BaseAct) getContext();
        }
        if (getParentFragment() instanceof BaseFrag) {
            mBaseFrag = (BaseFrag) getParentFragment();
        }
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            getLifecycle().addObserver(new FragLifeDebug(getClass().getSimpleName()));
        }
        Intent intent = mBaseAct.getIntent();
        if (intent != null) {
            handleIntent(intent);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            handleArgument(bundle);
        }
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutId(), container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        dispatchFirstVisible();
    }

//    private boolean mIsVisibleToUser;
//    private boolean mIsCallResume;

//    @Override
//    public void onResume() {
//        super.onResume();
//        mIsCallResume = true;
//        if (mIsVisibleToUser) {
//            dispatchFirstVisible();
//        }
//        dispatchFirstVisible();
//    }
//
//    @Override
//    @CallSuper
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        mIsVisibleToUser = isVisibleToUser;
//        if (getActivity() != null) {
//            if (mIsCallResume && mIsVisibleToUser) {
//                dispatchFirstVisible();
//            }
//        }
//    }
//
//    /**
//     * The method will only be called if there are nested fragments in the fragment.
//     * Solve the problem which the visibility of multiple child fragments is true during initialization.
//     * The basis is that as long as the visibility of the parent fragment is false, the visibility of the child fragment is also false
//     */
//    @Override
//    @CallSuper
//    public void onAttachFragment(Fragment childFragment) {
//        boolean isVisibleToUser = getUserVisibleHint();
//        if (!isVisibleToUser) {
//            if (childFragment.getUserVisibleHint()) {
//                childFragment.setUserVisibleHint(false);
//            }
//        }
//    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        mBaseAct = null;
        mBaseFrag = null;
    }

    protected void handleIntent(@NonNull Intent intent) {

    }

    protected void handleArgument(@NonNull Bundle bundle) {

    }

    protected abstract int layoutId();

    private void dispatchFirstVisible() {
        if (mIsFirstVisible) {
            onFirstVisible();
            mIsFirstVisible = false;
        }
    }

    /**
     * This method is called when fragment is first visible
     */
    protected void onFirstVisible() {

    }

    /**
     * Put shared data for easy access by other components.
     *
     * @param key   The name of shared data.
     * @param value The value of shared data.
     */
    protected void put(@NonNull String key, @NonNull Object value) {
        ViewModelProviders.of(mBaseAct).get(ShareDataVM.class).put(key, value);
    }

    /**
     * Get the value corresponding to the key
     *
     * @param key The name of shared data.
     * @return The value of shared data.
     */
    protected @NonNull <V> V get(@NonNull String key) {
        return ViewModelProviders.of(mBaseAct).get(ShareDataVM.class).get(key);
    }
}
