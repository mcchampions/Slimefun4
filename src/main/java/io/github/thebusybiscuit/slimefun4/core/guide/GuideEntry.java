package io.github.thebusybiscuit.slimefun4.core.guide;

import lombok.Getter;

class GuideEntry<T> {

    private final T object;
    @Getter
    private int page;

    GuideEntry(T object, int page) {
        this.object = object;
        this.page = page;
    }

    
    public T getIndexedObject() {
        return object;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
