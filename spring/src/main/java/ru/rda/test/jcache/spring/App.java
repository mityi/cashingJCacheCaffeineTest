package ru.rda.test.jcache.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class App {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        AnnotationsExample annotations = context.getBean(AnnotationsExample.class);
        annotations.create("1", new ValueObject("spring"));
        ValueObject graph = annotations.get("1");

        System.out.println(graph);

        org.springframework.cache.CacheManager manager =
                context.getBean(org.springframework.cache.CacheManager.class);

        org.springframework.cache.Cache sCache = manager.getCache("graph");
        sCache.getName();


//region concurrent hash map
//        org.springframework.cache.CacheManager manager =
//                context.getBean(org.springframework.cache.concurrent.ConcurrentMapCacheManager.class);
//endregion

//region caffeine
//        org.springframework.cache.CacheManager manager =
//                context.getBean(org.springframework.cache.caffeine.CaffeineCacheManager.class);
//endregion

//region JCache
//        org.springframework.cache.CacheManager manager =
//                context.getBean(org.springframework.cache.jcache.JCacheCacheManager.class);
//endregion

    }

}