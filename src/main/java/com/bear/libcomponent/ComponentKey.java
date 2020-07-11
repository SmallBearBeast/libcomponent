package com.bear.libcomponent;

import java.util.Objects;

class ComponentKey {
    Class clz;

    Object tag;

    public ComponentKey(Class clz, Object tag) {
        this.clz = clz;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentKey that = (ComponentKey) o;
        return Objects.equals(clz, that.clz) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clz, tag);
    }
}
