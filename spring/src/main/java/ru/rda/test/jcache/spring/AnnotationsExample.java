package ru.rda.test.jcache.spring;

import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;

@CacheDefaults(cacheName = "graph")
@Service
public class AnnotationsExample {

    @CacheResult
    public ValueObject get(String key) {
            /* Stuff */
        return new ValueObject("default");
    }

    @CacheRemove
    public void remove(String key) {
            /* Stuff */
    }

    @CacheRemoveAll
    public void removeAll() {
            /* Stuff */
    }

    @CachePut
    public void create(@CacheKey String key, @CacheValue ValueObject graph) {
            /* Stuff */
    }

}
