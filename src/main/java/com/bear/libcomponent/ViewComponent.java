package com.bear.libcomponent;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;


public abstract class ViewComponent<M> extends LifeComponent {
    private static final int INIT_COUNT = 32;
    private M mDependence;
    private View mContentView;
    private ComponentAct mComActivity;
    private SparseArray<View> mViewIdArray;

    void attachView(View contentView) {
        mContentView = contentView;
        if (contentView != null) {
            mViewIdArray = new SparseArray<>(INIT_COUNT);
        } else {
            if (mViewIdArray != null) {
                mViewIdArray.clear();
                mViewIdArray = null;
            }
        }
    }

    void attachDependence(M dependence) {
        mDependence = dependence;
    }

    void attachActivity(ComponentAct activity) {
        mComActivity = activity;
    }

    protected View clickListenerAndGetView(View.OnClickListener listener, @IdRes int viewId) {
        View view = findViewById(viewId);
        clickListener(listener, viewId);
        return view;
    }

    protected void clickListener(View.OnClickListener listener, @IdRes int... viewIds) {
        for (int id : viewIds) {
            if (mViewIdArray.get(id) != null) {
                mViewIdArray.get(id).setOnClickListener(listener);
            } else {
                findViewById(id).setOnClickListener(listener);
            }
        }
    }

    protected <T extends View> T findViewById(@IdRes int viewId) {
        View view = mViewIdArray.get(viewId);
        if (view == null) {
            view = mContentView.findViewById(viewId);
            mViewIdArray.put(viewId, view);
        }
        return (T) view;
    }

    protected void clear(Object... objects) {
        for (Object object : objects) {
            object = null;
        }
    }

    @CallSuper
    protected void onDestroy() {
        mDependence = null;
        attachView(null);
    }

    protected void onCreateView() {

    }

    protected void onDestroyView() {

    }

    protected void onFirstVisible() {

    }

    protected void onBackPressed() {

    }

    public M getDependence() {
        return mDependence;
    }

    public View getContentView() {
        return mContentView;
    }

    public ComponentAct getComActivity() {
        return mComActivity;
    }
}
