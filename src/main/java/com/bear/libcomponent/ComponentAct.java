package com.bear.libcomponent;

public abstract class ComponentAct extends BaseAct {
    protected <C extends IComponent> void regComponent(C component, Object tag) {
        ComponentService.get().regComponent(this, component, tag);
    }

    protected <C extends IComponent> void regComponent(C component) {
        ComponentService.get().regComponent(this, component);
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
        ComponentService.get().onBackPressed(this);
    }
}
