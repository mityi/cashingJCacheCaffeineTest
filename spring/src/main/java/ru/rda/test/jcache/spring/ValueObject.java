package ru.rda.test.jcache.spring;

import java.io.Serializable;

public class ValueObject implements Serializable {
    public String value = "";

    public ValueObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ValueObject{" +
                "value='" + value + '\'' +
                '}';
    }
}
