package io.github.thebusybiscuit.slimefun4.core.guide;

import lombok.Getter;
import lombok.Setter;

class GuideEntry<T> {
    private final T object;
    @Setter
    @Getter
    private int page;

    GuideEntry(T object, int page) {
        this.object = object;
        this.page = page;
    }

    public T getIndexedObject() {
        return object;
    }

}
