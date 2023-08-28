package com.bear.libcomponent.component;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bear.libcomponent.base.BaseFrag;

public abstract class ComponentFrag extends BaseFrag {

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
        ComponentService.get().dispatchOnAttach(context);
    }

    @Nullable
    @Override
    @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        ComponentService.get().dispatchOnCreateView(contentView);
        return contentView;
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        ComponentService.get().dispatchOnDestroyView();
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        ComponentService.get().onDetach();
    }

    @Override
    protected void onFirstVisible() {
        ComponentService.get().onFirstVisible();
    }

    public void regFragComponent(IComponent component, Object tag) {
        ComponentService.get().regFragComponent(this, component, tag);
    }

    public void regFragComponent(IComponent component) {
        ComponentService.get().regFragComponent(this, component);
    }

    public void regFragViewComponent(IComponent component, Object tag) {
        ComponentService.get().regFragViewComponent(this, component, tag);
    }

    public void regFragViewComponent(IComponent component) {
        ComponentService.get().regFragViewComponent(this, component);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        return ComponentService.get().getComponent(clz, tag);
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return ComponentService.get().getComponent(clz);
    }
}
