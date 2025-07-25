package j2ts_awt_swing.impl;

import elemental2.core.JsArray;

import java.util.Iterator;
import java.util.function.Consumer;

public final class Iter {
    public static <T> Iterable<T> iter(JsArray<T> arr) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int idx = 0;

                    @Override
                    public boolean hasNext() {
                        return idx < arr.length;
                    }

                    @Override
                    public T next() {
                        return arr.getAt(idx++);
                    }
                };
            }

            @Override
            public void forEach(Consumer<? super T> action) {
                arr.forEach((e, i, arr) -> {
                    action.accept(e);
                    return null;
                });
            }
        };
    }
}
