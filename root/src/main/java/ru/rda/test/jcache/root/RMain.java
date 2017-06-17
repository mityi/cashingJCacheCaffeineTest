package ru.rda.test.jcache.root;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;
import javax.cache.integration.CompletionListenerFuture;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import javax.cache.spi.CachingProvider;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RMain {

    public static final Map<String, MyObject> repository = new ConcurrentHashMap<>();

    static {
        repository.put("1", new MyObject("v"));
        repository.put("2", new MyObject("a"));
        repository.put("3", new MyObject("l"));
        repository.put("4", new MyObject("u"));
        repository.put("5", new MyObject("e"));
    }

    public static void main(String[] args) throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, ExecutionException, InterruptedException {

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager manager = provider.getCacheManager();

        MutableConfiguration<String, MyObject> configuration =
                new MutableConfiguration<String, MyObject>()
                        .setTypes(String.class, MyObject.class)
                        .setManagementEnabled(true)
                        .setStatisticsEnabled(true)
                        .setStoreByValue(true) //store-by-value or store-by-reference
                        .setCacheLoaderFactory(FactoryBuilder.factoryOf(MyLoader.class))
                        .setCacheWriterFactory(FactoryBuilder.factoryOf(MyWriter.class))
                        .setReadThrough(true);

        Cache<String, MyObject> cache = manager.createCache("root", configuration);

        final Factory<MyCacheEntryListener> myCacheEntryListenerFactory =
                FactoryBuilder.factoryOf(MyCacheEntryListener.class);
        CacheEntryListenerConfiguration listenerConfiguration =
                new MutableCacheEntryListenerConfiguration<>(
                        myCacheEntryListenerFactory,
                        null,
                        false,
                        true);
        cache.registerCacheEntryListener(listenerConfiguration);

        CompletionListenerFuture completionListener = new CompletionListenerFuture();
        cache.loadAll(Collections.singleton("2"), true, completionListener);
        completionListener.get();

        Map<String, MyObject> all = cache.getAll(new HashSet<>(repository.keySet()));

        MyObject graph = cache.get("1");
        System.out.println(graph);

        graph.value = "FFFF"; //setStoreByValue
        System.out.println(cache.get("1"));

        cache.put("1", new MyObject("D"));
        cache.getAndPut("1", new MyObject("cool"));

// which is better?
// using a lock based API?
//        cache.lock(“1”);
//        data = cache.get(“1”);
//        cache.put(“1”, "xxxx");
//        cache.unlock(“key”);
// using an entry processor?
        cache.invoke("1", new MyEntryProcessor());

        System.out.println(cache.get("1"));

    }

    public static class MyEntryProcessor implements EntryProcessor<String, MyObject, Object> {
        @Override
        public Object process(MutableEntry<String, MyObject> entry, Object... arguments) throws EntryProcessorException {
            return entry.getValue().value = "JSR107 is " + entry.getValue().value;
        }
    }

    public static class MyCacheEntryListener
            implements CacheEntryCreatedListener<String, MyObject>,
            CacheEntryUpdatedListener<String, MyObject> {

        @Override
        public void onCreated(Iterable<CacheEntryEvent<? extends String, ? extends MyObject>> events) throws CacheEntryListenerException {
            events.forEach(event -> System.out.println(
                    "Created : " + event.getKey() + " with value : " + event.getValue()));
        }

        @Override
        public void onUpdated(Iterable<CacheEntryEvent<? extends String, ? extends MyObject>> events) throws CacheEntryListenerException {
            events.forEach(event -> System.out.println(
                    "Updated : " + event.getKey() + " with value : " + event.getValue()));
        }
    }

    public static class MyLoader implements CacheLoader<String, MyObject> {

        @Override
        public MyObject load(String key) throws CacheLoaderException {
            return repository.get(key);
        }

        @Override
        public Map<String, MyObject> loadAll(Iterable<? extends String> keys)
                throws CacheLoaderException {
            return StreamSupport.stream(keys.spliterator(), false)
                    .collect(Collectors.toMap(key -> key, repository::get));
        }
    }

    public static class MyWriter implements CacheWriter<String, MyObject> {

        @Override
        public void write(Cache.Entry<? extends String, ? extends MyObject> entry)
                throws CacheWriterException {
            repository.put(entry.getKey(), entry.getValue());
        }

        @Override
        public void writeAll(
                Collection<Cache.Entry<? extends String, ? extends MyObject>> entries)
                throws CacheWriterException {
            Map<? extends String, ? extends MyObject> collect = entries.stream()
                    .collect(Collectors.toMap(Cache.Entry::getKey, Cache.Entry::getValue));
            repository.putAll(collect);
        }

        @Override
        public void delete(Object key) throws CacheWriterException {
            repository.remove(key);
        }

        @Override
        public void deleteAll(Collection<?> keys) throws CacheWriterException {
            keys.forEach(repository::remove);
        }
    }
}


