package me.squeeglii.plugin.eventfw.config.data;

import me.squeeglii.plugin.eventfw.config.Checker;

import java.util.Objects;

public class Key<T> {

    private final String key;

    public Key(String key) {
        Checker.nullParam(key, "Key String");
        String modifiedKey = key.trim().toLowerCase();

        if(modifiedKey.isEmpty())
            throw new IllegalArgumentException("Key cannot be made of only whitespace.");

        this.key = modifiedKey;
    }

    public String get() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key<?> key1)) return false;
        return Objects.equals(key, key1.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public static <T> Key<T> of(String string) {
        return new Key<>(string);
    }
}