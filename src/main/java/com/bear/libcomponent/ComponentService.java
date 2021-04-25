package com.bear.libcomponent;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ComponentService {
    private Map<LifecycleOwner, Map<ComponentKey, IComponent>> mLifecycleActComponentMap = new HashMap<>();
    private Map<LifecycleOwner, Map<ComponentKey, IComponent>> mLifecycleFragComponentMap = new HashMap<>();

    public static ComponentService get() {
        return SingleTon.INSTANCE;
    }

    public <C extends IComponent> void regComponent(ComponentAct activity, final C component, Object tag) {
        if (component != null) {
            if (component instanceof ViewComponent) {
                ((ViewComponent) component).attachDependence(activity);
                ((ViewComponent) component).attachView(activity.getDecorView());
            }
            if (!mLifecycleActComponentMap.containsKey(activity)) {
                mLifecycleActComponentMap.put(activity, new HashMap<ComponentKey, IComponent>());
            }
            Map<ComponentKey, IComponent> componentMap = mLifecycleActComponentMap.get(activity);
            if (componentMap != null) {
                componentMap.put(new ComponentKey(component.getClass(), tag), component);
            }
            activity.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    component.onStateChanged(source, event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.getLifecycle().removeObserver(this);
                        mLifecycleActComponentMap.remove(source);
                    }
                }
            });
        }
    }

    public <C extends IComponent> void regComponent(ComponentAct activity, C component) {
        regComponent(activity, component, null);
    }

    public void regComponent(ComponentFrag fragment, final IComponent component, Object tag) {
        if (component != null) {
            if (component instanceof ViewComponent) {
                ((ViewComponent) component).attachActivity(fragment.getComActivity());
                ((ViewComponent) component).attachDependence(fragment);
            }
            if (!mLifecycleFragComponentMap.containsKey(fragment)) {
                mLifecycleFragComponentMap.put(fragment, new HashMap<ComponentKey, IComponent>());
            }
            Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
            if (componentMap != null) {
                componentMap.put(new ComponentKey(component.getClass(), tag), component);
            }
            fragment.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    component.onStateChanged(source, event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.getLifecycle().removeObserver(this);
                        mLifecycleFragComponentMap.remove(source);
                    }
                }
            });
        }
    }

    public void regComponent(ComponentFrag fragment, IComponent component) {
        regComponent(fragment, component, null);
    }

    public <C> C getComponent(Class<C> clz) {
        return getComponent(clz, null);
    }

    public <C> C getComponent(Class<C> clz, Object tag) {
        if (!clz.isInterface()) {
            throw new NoInterfaceException();
        }
        ComponentKey componentKey = new ComponentKey(clz, tag);
        C component = getComponentFromMap(mLifecycleActComponentMap, componentKey);
        if (component == null) {
            component = getComponentFromMap(mLifecycleFragComponentMap, componentKey);
        }
        if (component == null) {
            component = getProxyComponent(clz);
        }
        return component;
    }

    private <C> C getComponentFromMap(Map<LifecycleOwner, Map<ComponentKey, IComponent>> lifecycleComponentMap, ComponentKey componentKey) {
        for (Map.Entry<LifecycleOwner, Map<ComponentKey, IComponent>> mapEntry : lifecycleComponentMap.entrySet()) {
            Map<ComponentKey, IComponent> componentMap = mapEntry.getValue();
            if (componentMap != null) {
                for (Map.Entry<ComponentKey, IComponent> entry: componentMap.entrySet()) {
                    if (entry.getKey().equals(componentKey)) {
                        return (C) entry.getValue();
                    }
                }
            }
        }
        return null;
    }

    private <C> C getProxyComponent(Class<C> clz) {
        try {
            InvocationHandler invocationHandler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) {
                    return null;
                }
            };
            return (C) Proxy.newProxyInstance(clz.getClassLoader(), new Class[] {clz}, invocationHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void onCreateView(ComponentFrag fragment, View contentView) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).attachView(contentView);
                ((ViewComponent) component).onCreateView();
            }
        }
    }

    void onDestroyView(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).attachView(null);
                ((ViewComponent) component).onDestroyView();
            }
        }
    }

    void onAttach(ComponentFrag fragment, ComponentAct activity) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).attachActivity(activity);
                ((ViewComponent) component).attachDependence(fragment);
            }
        }
    }

    void onDetach(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).attachActivity(null);
                ((ViewComponent) component).attachDependence(null);
            }
        }
    }

    void onFirstVisible(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleFragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).onFirstVisible();
            }
        }
    }

    void onBackPressed(ComponentAct activity) {
        Map<ComponentKey, IComponent> componentMap = mLifecycleActComponentMap.get(activity);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                ((ViewComponent) component).onBackPressed();
            }
        }
        List<Fragment> fragmentList = activity.getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            componentMap = mLifecycleFragComponentMap.get(fragment);
            if (componentMap != null) {
                for (IComponent component : componentMap.values()) {
                    ((ViewComponent) component).onBackPressed();
                }
            }
        }
    }

    private static class SingleTon {
        private static final ComponentService INSTANCE = new ComponentService();
    }

    private static class NoInterfaceException extends RuntimeException {
        private NoInterfaceException() {
            super("The class should be interface");
        }
    }

}
