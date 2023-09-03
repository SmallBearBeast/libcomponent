package com.bear.libcomponent.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.bear.libcomponent.provider.IBackPressedProvider;
import com.bear.libcomponent.provider.IMenuProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ComponentService {

    private final PageComponentStack pageComponentStack = new PageComponentStack();

    public static ComponentService get() {
        return SingleTon.INSTANCE;
    }

    private static class SingleTon {
        private static final ComponentService INSTANCE = new ComponentService();
    }

    public void init(Application app) {
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                pageComponentStack.createComponentMap(activity.hashCode());
            }

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPostDestroyed(@NonNull Activity activity) {
                pageComponentStack.destroyComponentMap(activity.hashCode());
            }
        });
    }

    public <C extends IComponent> void regActComponent(ComponentAct activity, @NonNull C component) {
        regActComponent(activity, component, null);
    }

    public <C extends IComponent> void regActComponent(ComponentAct activity, @NonNull C component, @Nullable Object tag) {
        if (pageComponentStack.containComponent(component.getClass(), tag)) {
            throw new RuntimeException("Can not register component with same type and tag");
        }
        if (component instanceof BaseComponent) {
            ((BaseComponent) component).attachContext(activity);
            ((BaseComponent) component).attachView(activity.getDecorView());
        }
        pageComponentStack.putComponent(component, tag);
        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                component.onStateChanged(source, event);
                if (event == Lifecycle.Event.ON_DESTROY) {
                    source.getLifecycle().removeObserver(this);
                    pageComponentStack.removeComponent(component, tag);
                }
            }
        });
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

    private <C extends IComponent> void regFragComponent(ComponentFrag fragment, Lifecycle lifecycle, @NonNull C component, @Nullable Object tag) {
        if (pageComponentStack.containComponent(component.getClass(), tag)) {
            throw new RuntimeException("Can not register component with same type and tag");
        }
        if (component instanceof BaseComponent) {
            ((BaseComponent) component).attachContext(fragment.getContext());
            ((BaseComponent) component).attachView(fragment.getView());
        }
        pageComponentStack.putComponent(component, tag);
        lifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                component.onStateChanged(source, event);
                if (event == Lifecycle.Event.ON_DESTROY) {
                    source.getLifecycle().removeObserver(this);
                    pageComponentStack.removeComponent(component, tag);
                }
            }
        });
    }

    public <C extends IComponent> C getComponent(Class<C> clz) {
        return getComponent(clz, null);
    }

    public <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
        if (pageComponentStack.usePrevPageComponent(clz, tag)) {
            throw new RuntimeException("Can not use prev page component");
        }
        C component = pageComponentStack.getComponent(clz, tag);
        if (component == null) {
            component = getProxyComponent(clz);
            pageComponentStack.putComponent(component, tag);
        }
        return component;
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

    void dispatchOnCreateView(View contentView) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).attachView(contentView);
                    ((FragmentComponent) component).onCreateView();
                }
            }
        }
    }

    void dispatchOnDestroyView() {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).onDestroyView();
                    ((FragmentComponent) component).attachView(null);
                }
            }
        }
    }

    void dispatchOnAttach(Context context) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).attachContext(context);
                }
            }
        }
    }

    void dispatchOnDetach() {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((BaseComponent) component).attachContext(null);
                }
            }
        }
    }

    void dispatchOnFirstVisible() {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof FragmentComponent) {
                    ((FragmentComponent) component).onFirstVisible();
                }
            }
        }
    }

    void dispatchOnBackPressed() {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof IBackPressedProvider) {
                    ((IBackPressedProvider) component).onBackPressed();
                }
            }
        }
    }

    boolean dispatchOnCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        boolean created = false;
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof IMenuProvider) {
                    created |= ((IMenuProvider) component).onCreateOptionsMenu(menu, menuInflater);
                }
            }
        }
        return created;
    }

    boolean dispatchOnOptionsItemSelected(MenuItem item) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        boolean created = false;
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof IMenuProvider) {
                    created |= ((IMenuProvider) component).onOptionsItemSelected(item);
                }
            }
        }
        return created;
    }

    void dispatchOnCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof IMenuProvider) {
                    ((IMenuProvider) component).onCreateContextMenu(menu, v, menuInfo);
                }
            }
        }
    }

    boolean dispatchOnContextItemSelected(MenuItem item) {
        Map<ComponentKey, IComponent> componentMap = pageComponentStack.getTopComponentMap();
        boolean created = false;
        if (componentMap != null) {
            for (IComponent component : componentMap.values()) {
                if (component instanceof IMenuProvider) {
                    created |= ((IMenuProvider) component).onContextItemSelected(item);
                }
            }
        }
        return created;
    }

    // 防止调用之前页面的组件。
    private static class PageComponentStack extends LinkedList<Pair<Integer, Map<ComponentKey, IComponent>>> {

        private void createComponentMap(int hashCode) {
            push(new Pair<>(hashCode, new HashMap()));
        }

        private void destroyComponentMap(int hashCode) {
            int removeIndex = -1;
            for (int i = 0; i < size(); i++) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = get(i);
                if (pair != null && pair.first != null && pair.first == hashCode) {
                    removeIndex = i;
                    break;
                }
            }
            if (removeIndex != -1) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = remove(removeIndex);
                if (pair != null && pair.second != null) {
                    pair.second.clear();
                }
            }
        }

        private void putComponent(IComponent component, Object tag) {
            Pair<Integer, Map<ComponentKey, IComponent>> pair = peek();
            Map<ComponentKey, IComponent> map = null;
            if (pair != null) {
                map = pair.second;
            }
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(new ComponentKey(component.getClass(), tag), component);
        }

        private <C extends IComponent> C getComponent(Class<C> clz, Object tag) {
            if (!isEmpty()) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = peek();
                if (pair != null) {
                    Map<ComponentKey, IComponent> map = pair.second;
                    if (map != null) {
                        ComponentKey componentKey = new ComponentKey(clz, tag);
                        return (C) map.get(componentKey);
                    }
                }
            }
            return null;
        }

        private void removeComponent(IComponent component, Object tag) {
            if (!isEmpty()) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = peek();
                if (pair != null) {
                    Map<ComponentKey, IComponent> map = pair.second;
                    if (map != null) {
                        ComponentKey componentKey = new ComponentKey(component.getClass(), tag);
                        map.remove(componentKey);
                    }
                }
            }
        }

        private Map<ComponentKey, IComponent> getTopComponentMap() {
            Pair<Integer, Map<ComponentKey, IComponent>> pair = peek();
            if (pair != null && pair.second != null) {
                return pair.second;
            }
            return new HashMap<>();
        }

        private <C extends IComponent> boolean usePrevPageComponent(Class<C> clz, Object tag) {
            if (!isEmpty()) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = pop();
                if (pair != null) {
                    Map<ComponentKey, IComponent> map = pair.second;
                    if (map != null) {
                        ComponentKey componentKey = new ComponentKey(clz, tag);
                        for (Pair<Integer, Map<ComponentKey, IComponent>> pairMap : this) {
                            if (pairMap.second != null && pairMap.second.containsKey(componentKey)) {
                                return true;
                            }
                        }
                        push(pair);
                    }
                }
            }
            return false;
        }

        private <C extends IComponent> boolean containComponent(Class<C> clz, Object tag) {
            if (!isEmpty()) {
                Pair<Integer, Map<ComponentKey, IComponent>> pair = peek();
                if (pair != null) {
                    Map<ComponentKey, IComponent> map = pair.second;
                    if (map != null) {
                        ComponentKey componentKey = new ComponentKey(clz, tag);
                        return map.containsKey(componentKey);
                    }
                }
            }
            return false;
        }
    }
}
