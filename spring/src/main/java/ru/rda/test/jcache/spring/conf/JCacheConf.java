package ru.rda.test.jcache.spring.conf;


import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.github.benmanes.caffeine.jcache.copy.Copier;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rda.test.jcache.spring.ValueObject;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

//@Configuration
public class JCacheConf {

    @Bean
    public CachingProvider caffeineCachingProvider() {
        return Caching.getCachingProvider("com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider");
    }

    @Bean
    public JCacheCacheManager getSpringCacheManager(CachingProvider cachingProvider) {
        CacheManager cacheManager = cachingProvider.getCacheManager();

        cacheManager.createCache("graph2", caffeine());

        cacheManager.createCache("graph", jCache());

        return new JCacheCacheManager(cacheManager);
    }

    private MutableConfiguration jCache() {
                return new MutableConfiguration()
                        .setManagementEnabled(true)
                        .setStatisticsEnabled(true)
                        .setStoreByValue(true) //store-by-value or store-by-reference
                        .setReadThrough(true);
    }

    private CaffeineConfiguration<String, ValueObject> caffeine() {
        Duration expiryDuration = new Duration(TimeUnit.MINUTES, 60l);
        AccessedExpiryPolicy accessedExpiryPolicy = new AccessedExpiryPolicy(expiryDuration);
        CaffeineConfiguration<String, ValueObject> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setExpiryPolicyFactory(FactoryBuilder.factoryOf(accessedExpiryPolicy));
        caffeineConfiguration.setCopierFactory(Copier::identity);
        return caffeineConfiguration;
    }
}
