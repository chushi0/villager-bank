package online.cszt0.mcmod.bank.util;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Value<V> implements Setter<V>, Getter<V> {
    private final Getter<V> getter;
    private final Setter<V> setter;

    @Override
    public V get() {
        return getter.get();
    }

    @Override
    public void set(V value) {
        setter.set(value);
    }
}
