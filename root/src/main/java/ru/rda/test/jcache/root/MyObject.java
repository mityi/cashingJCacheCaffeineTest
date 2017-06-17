package ru.rda.test.jcache.root;

import java.io.Serializable;

public class MyObject implements Serializable {
    public String value = "";

    public MyObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MyObject{" +
                "value='" + value + '\'' +
                '}';
    }
}
