package ru.rda.test.jcache.spring.conf;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

//@Configuration
public class JCaffeineConf {

    @Bean(name = "caffeineCacheManager")
    public CaffeineCacheManager getSpringCacheManager() {
        Caffeine caffeine = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(500, TimeUnit.SECONDS);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(Collections.singleton("graph"));
        return cacheManager;
    }
}
