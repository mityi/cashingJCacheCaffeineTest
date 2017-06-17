package ru.rda.test.jcache.caffeine;

public class MyCaffeineValueObject {
    public String value = "";

    public MyCaffeineValueObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MyCaffeineValueObject{" +
                "value='" + value + '\'' +
                '}';
    }
}
