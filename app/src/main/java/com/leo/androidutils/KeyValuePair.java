package com.leo.androidutils;

import androidx.annotation.NonNull;

/**
 * Stores a key and a value. Intended for displaying a list of values from some key-value based storage while storing each value's key.<br>
 * Created by Leo40Git on 28/03/2020.
 * @author Leo40Git
 */
public class KeyValuePair {
    private String key;
    private String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
