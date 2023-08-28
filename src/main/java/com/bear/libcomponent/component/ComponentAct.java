package com.bear.libcomponent.component;

import com.bear.libcomponent.base.BaseAct;

public abstract class ComponentAct extends BaseAct {
    protected <C extends IComponent> void regActComponent(C component, Object tag) {
        ComponentService.get().regActComponent(this, component, tag);
    }

    protected <C extends IComponent> void regActComponent(C component) {
        ComponentService.get().regActComponent(this, component);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        return ComponentService.get().getComponent(clz, tag);
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return ComponentService.get().getComponent(clz);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ComponentService.get().onBackPressed();
    }
}
