package ru.rda.test.jcache.caffeine;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CMain <K1 extends Object, V1 extends Object> {

    private static final Map<String, MyCaffeineValueObject> repository = new ConcurrentHashMap<>();

    static {
        repository.put("1", new MyCaffeineValueObject("v"));
        repository.put("2", new MyCaffeineValueObject("a"));
        repository.put("3", new MyCaffeineValueObject("l"));
        repository.put("4", new MyCaffeineValueObject("u"));
        repository.put("5", new MyCaffeineValueObject("e"));
    }

    static CacheWriter<String, MyCaffeineValueObject> writer = new CacheWriter<String, MyCaffeineValueObject>() {
        @Override
        public void write(String key, MyCaffeineValueObject value) {
            repository.put(key, value);
        }

        @Override
        public void delete(String key, MyCaffeineValueObject value, RemovalCause cause) {
            repository.remove(key, value);
        }
    };

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Caffeine<String, MyCaffeineValueObject> caffeine = Caffeine.newBuilder()
                .removalListener((RemovalListener<String, MyCaffeineValueObject>)
                                (key, value, cause) ->
                                        print("Key : " + key + " with value : " + value + " and cause : " + cause))
//                .writer(writer)
//                .weigher(CalculatesTheWeightsOfCacheEntries).maximumWeight(weight)
//                .executor(ForkJoinPool.commonPool())
                .recordStats()
//                .weakKeys()
//                .softValues()
                .refreshAfterWrite(2, TimeUnit.SECONDS)
                .maximumSize(10_000)
                .expireAfterWrite(5, TimeUnit.SECONDS);

//        Cache<String, MyCaffeineValueObject> cache = caffeine.build();
//        MyCaffeineValueObject one = cache.get("1", s -> new MyCaffeineValueObject("eee"));
//        System.out.println(one);
//        one = cache.get("1", s -> new MyCaffeineValueObject("eee2"));
//        System.out.println(one);

        LoadingCache<String, MyCaffeineValueObject> loadingCache = caffeine
                .build(CMain::getValue);
        MyCaffeineValueObject graph = loadingCache.get("1");
        System.out.println(graph);

        repository.put("1", new MyCaffeineValueObject("it's work"));
        loadingCache.refresh("1");
        graph = loadingCache.get("1");
        System.out.println(graph);

        AsyncLoadingCache<String, MyCaffeineValueObject> asyncLoadingCache = caffeine
                .buildAsync((key, executor) -> CompletableFuture.supplyAsync(() -> getValue(key), executor));

        CompletableFuture<MyCaffeineValueObject> graphCompletableFuture = asyncLoadingCache.get("1");
        System.out.println(graphCompletableFuture.get());

    }

    private static void print(String string) {
        System.out.println(string);
    }

    private static MyCaffeineValueObject getValue(String key) {
        return repository.get(key);
    }

}
