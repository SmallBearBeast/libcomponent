package com.bear.libcomponent.component;

import java.util.Objects;

class ComponentKey<C extends IComponent> {
    private final Class<C> clz;
    private final Object tag;

    public ComponentKey(Class<C> clz, Object tag) {
        this.clz = clz;
        this.tag = tag;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        ComponentKey that = (ComponentKey) obj;
        return that.clz.isAssignableFrom(clz) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clz, tag);
    }
}
