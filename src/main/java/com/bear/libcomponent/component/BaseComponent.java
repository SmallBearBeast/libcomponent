package com.bear.libcomponent.component;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;

import com.bear.libcomponent.provider.IContextProvider;

public abstract class BaseComponent extends LifeComponent implements IContextProvider {
    private static final int INIT_COUNT = 32;

    private View contentView;

    private Context context;

    private SparseArray<View> viewIdArray;

    void attachView(View view) {
        contentView = view;
        if (contentView != null) {
            viewIdArray = new SparseArray<>(INIT_COUNT);
        } else {
            if (viewIdArray != null) {
                viewIdArray.clear();
                viewIdArray = null;
            }
        }
    }

    protected View findViewAndSetListener(View.OnClickListener listener, @IdRes int viewId) {
        View view = findViewById(viewId);
        setOnClickListener(listener, viewId);
        return view;
    }

    protected void setOnClickListener(View.OnClickListener listener, @IdRes int... viewIds) {
        for (int id : viewIds) {
            if (viewIdArray.get(id) != null) {
                viewIdArray.get(id).setOnClickListener(listener);
            } else {
                findViewById(id).setOnClickListener(listener);
            }
        }
    }

    protected <T extends View> T findViewById(@IdRes int viewId) {
        View view = viewIdArray.get(viewId);
        if (view == null) {
            view = contentView.findViewById(viewId);
            viewIdArray.put(viewId, view);
        }
        return (T) view;
    }

    public View getContentView() {
        return contentView;
    }

    @Override
    public void attachContext(Context c) {
        context = c;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ComponentAct getActivity() {
        if (context instanceof ComponentAct) {
            return (ComponentAct) context;
        }
        return null;
    }
}
