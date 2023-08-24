package com.bear.libcomponent.component;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ComponentService {
    private final Map<LifecycleOwner, Map<ComponentKey, IComponent>> actComponentMap = new HashMap<>();
    private final Map<LifecycleOwner, Map<ComponentKey, IComponent>> fragComponentMap = new HashMap<>();

    public static ComponentService get() {
        return SingleTon.INSTANCE;
    }

    private static class SingleTon {
        private static final ComponentService INSTANCE = new ComponentService();
    }

    public <C extends IComponent> void regActComponent(ComponentAct activity, C component) {
        regActComponent(activity, component, null);
    }

    public <C extends IComponent> void regActComponent(ComponentAct activity, C component, Object tag) {
        if (component != null) {
            if (component instanceof BaseComponent) {
                ((BaseComponent) component).attachContext(activity);
                ((BaseComponent) component).attachView(activity.getDecorView());
            }
            if (!actComponentMap.containsKey(activity)) {
                actComponentMap.put(activity, new HashMap<>());
            }
            Map<ComponentKey, IComponent> componentMap = actComponentMap.get(activity);
            if (componentMap != null) {
                componentMap.put(new ComponentKey(component.getClass(), tag), component);
            }
            activity.getLifecycle().addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    component.onStateChanged(source, event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.getLifecycle().removeObserver(this);
                        actComponentMap.remove(source);
                    }
                }
            });
        }
    }

    public <C extends IComponent> void regFragComponent(ComponentFrag fragment, C component) {
        regFragComponent(fragment, fragment.getLifecycle(), component, null);
    }

    public <C extends IComponent> void regFragComponent(ComponentFrag fragment, C component, Object tag) {
        regFragComponent(fragment, fragment.getLifecycle(), component, tag);
    }

    public <C extends IComponent> void regFragViewComponent(ComponentFrag fragment, C component) {
        regFragComponent(fragment, fragment.getViewLifecycleOwner().getLifecycle(), component, null);
    }

    public <C extends IComponent> void regFragViewComponent(ComponentFrag fragment, C component, Object tag) {
        regFragComponent(fragment, fragment.getViewLifecycleOwner().getLifecycle(), component, tag);
    }

    private <C extends IComponent> void regFragComponent(ComponentFrag fragment, Lifecycle lifecycle, C component, Object tag) {
        if (component != null) {
            if (component instanceof BaseComponent) {
                ((BaseComponent) component).attachContext(fragment.getContext());
                ((BaseComponent) component).attachView(fragment.getView());
            }
            if (!fragComponentMap.containsKey(fragment)) {
                fragComponentMap.put(fragment, new HashMap<>());
            }
            Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
            if (componentMap != null) {
                componentMap.put(new ComponentKey(component.getClass(), tag), component);
            }
            lifecycle.addObserver(new LifecycleEventObserver() {
                @Override
                public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    component.onStateChanged(source, event);
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        source.getLifecycle().removeObserver(this);
                        fragComponentMap.remove(source);
                    }
                }
            });
        }
    }

    public <C> C getComponent(Class<C> clz) {
        return getComponent(clz, null);
    }

    public <C> C getComponent(Class<C> clz, Object tag) {
        if (!clz.isInterface()) {
            throw new NoInterfaceException();
        }
        ComponentKey componentKey = new ComponentKey(clz, tag);
        C component = getComponentFromMap(actComponentMap, componentKey);
        if (component == null) {
            component = getComponentFromMap(fragComponentMap, componentKey);
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
            InvocationHandler invocationHandler = (proxy, method, args) -> null;
            return (C) Proxy.newProxyInstance(clz.getClassLoader(), new Class[] {clz}, invocationHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void dispatchOnCreateView(ComponentFrag fragment, View contentView) {
        Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).attachView(contentView);
                    ((FragmentComponent) component).onCreateView();
                }
            }
        }
    }

    void dispatchOnDestroyView(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).onDestroyView();
                    ((FragmentComponent) component).attachView(null);
                }
            }
        }
    }

    void dispatchOnAttach(ComponentFrag fragment, Context context) {
        Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).attachContext(context);
                }
            }
        }
    }

    void onDetach(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((BaseComponent) component).attachContext(null);
                }
            }
        }
    }

    void onFirstVisible(ComponentFrag fragment) {
        Map<ComponentKey, IComponent> componentMap = fragComponentMap.get(fragment);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).onFirstVisible();
                }
            }
        }
    }

    void onBackPressed(ComponentAct activity) {
        Map<ComponentKey, IComponent> componentMap = actComponentMap.get(activity);
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof BaseComponent) {
                    ((BaseComponent) component).onBackPressed();
                }
            }
        }
        List<Fragment> fragmentList = activity.getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            componentMap = fragComponentMap.get(fragment);
            if (componentMap != null) {
                for (IComponent component : componentMap.values()) {
                    if (component instanceof BaseComponent) {
                        ((BaseComponent) component).onBackPressed();
                    }
                }
            }
        }
    }

    private static class NoInterfaceException extends RuntimeException {
        private NoInterfaceException() {
            super("The class should be interface");
        }
    }

}
