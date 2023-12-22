package com.bear.libcomponent.component;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContainerComponent extends BaseComponent {
    private ContainerComponent parentComponent;
    private final Map<ComponentKey<?>, IComponent> subComponentMap = new HashMap<>();

    public ContainerComponent() {

    }

    public ContainerComponent(View view) {
        attachView(view);
    }

    public void regComponent(IComponent component) {
        regComponent(component, null);
    }

    public void regComponent(IComponent component, Object tag) {
        ComponentKey<?> componentKey = new ComponentKey<>(component.getClass(), tag);
        if (subComponentMap.containsKey(componentKey)) {
            throw new RuntimeException("Can not register component with same type and tag");
        }
        if (component instanceof BaseComponent) {
            BaseComponent baseComponent = (BaseComponent) component;
            baseComponent.attachContext(getContext());
            if (baseComponent.getContentView() != null) {
                baseComponent.attachView(getContentView());
            }
        }
        if (component instanceof ContainerComponent) {
            ((ContainerComponent) component).parentComponent = this;
        }
        subComponentMap.put(componentKey, component);
    }

    public <C extends IComponent> void unRegComponent(Class<C> clz) {
        unRegComponent(clz, null);
    }

    public <C extends IComponent> void unRegComponent(Class<C> clz, Object tag) {
        ComponentKey<?> targetKey = new ComponentKey<>(clz, tag);
        subComponentMap.remove(targetKey);
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return getComponent(clz, null);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        ComponentKey<?> targetKey = new ComponentKey<>(clz, tag);
        IComponent targetComponent = travel(targetKey, null);
        if (targetComponent != null) {
            return (C) targetComponent;
        }
        ContainerComponent pComponent = parentComponent;
        ContainerComponent excludeComponent = this;
        while (targetComponent == null && pComponent != null) {
            targetComponent = pComponent.travel(targetKey, excludeComponent);
            excludeComponent = pComponent;
            pComponent = pComponent.parentComponent;
        }
        if (targetComponent != null) {
            return (C) targetComponent;
        }
        // 没有过滤掉已经查询的组件
        targetComponent = ComponentService.get().getComponent(clz, tag);
        if (targetComponent != null) {
            return (C) targetComponent;
        }
        return getProxyComponent(clz);
    }

    public <C extends IComponent> C travel(ComponentKey<?> targetKey, IComponent excludeComponent) {
        if (subComponentMap.containsKey(targetKey)) {
            return (C) subComponentMap.get(targetKey);
        }
        for (IComponent component : subComponentMap.values()) {
            if (component == excludeComponent) {
                continue;
            }
            if (component instanceof ContainerComponent) {
                IComponent targetComponent = ((ContainerComponent)component).travel(targetKey, excludeComponent);
                if (targetComponent != null) {
                    return (C) targetComponent;
                }
            }
        }
        return null;
    }

    private <C extends IComponent> C getProxyComponent(Class<C> clz) {
        try {
            InvocationHandler invocationHandler = (proxy, method, args) -> null;
            return (C) Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, invocationHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void foreach(ForeachAction foreachAction) {
        for (IComponent component : subComponentMap.values()) {
            foreachAction.foreach(component);
            if (component instanceof ContainerComponent) {
                ((ContainerComponent) component).foreach(foreachAction);
            }
        }
    }

    @Override
    protected void attachView(View view) {
        super.attachView(view);
        for (IComponent component : subComponentMap.values()) {
            if (component instanceof BaseComponent) {
                if (view == null || ((BaseComponent) component).getContentView() == null) {
                    ((BaseComponent) component).attachView(view);
                }
            }
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
        Iterator<IComponent> iterator = subComponentMap.values().iterator();
        while (iterator.hasNext()) {
            IComponent component = iterator.next();
            component.onStateChanged(source, event);
            if (event == Lifecycle.Event.ON_DESTROY) {
                iterator.remove();
            }
        }
    }

    interface ForeachAction {
        void foreach(IComponent component);
    }
}
