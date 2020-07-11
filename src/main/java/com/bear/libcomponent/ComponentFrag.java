package com.bear.libcomponent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ComponentFrag extends BaseFrag {
    private ComponentAct mComActivity;

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ComponentAct) {
            mComActivity = (ComponentAct) context;
            ComponentService.get().onAttach(this, (ComponentAct) context);
        }
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        ComponentService.get().onCreateView(this, contentView);
        return contentView;
    }

    public void regComponent(IComponent component, Object tag) {
        ComponentService.get().regComponent(this, component, tag);
    }

    public void regComponent(IComponent component) {
        ComponentService.get().regComponent(this, component);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        return ComponentService.get().getComponent(clz, tag);
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return ComponentService.get().getComponent(clz);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        ComponentService.get().onDestroyView(this);
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        ComponentService.get().onDetach(this);
        mComActivity = null;
    }

    @Override
    protected void onFirstVisible() {
        ComponentService.get().onFirstVisible(this);
    }

    public ComponentAct getComActivity() {
        return mComActivity;
    }
}
