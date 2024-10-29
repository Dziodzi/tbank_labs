package io.github.dziodzi.repository;

import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.entity.dto.LocationDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore<K, T> {
    private final ConcurrentHashMap<K, T> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, ConcurrentHashMap<Long, T>> snapshotStore = new ConcurrentHashMap<>();

    public ConcurrentHashMap<K, T> getAll() {
        return store;
    }

    public T get(K key) {
        return store.get(key);
    }

    public T create(K key, T entity) {
        store.put(key, entity);
        return entity;
    }

    public void update(K key, T entity) {
        store.put(key, entity);
        createSnapshot(key, entity);
    }

    public void delete(K key) {
        store.remove(key);
        snapshotStore.remove(key);
    }

    public void createSnapshot(K key, T entity) {
        long timestamp = System.currentTimeMillis();
        snapshotStore.putIfAbsent(key, new ConcurrentHashMap<>());
        snapshotStore.get(key).put(timestamp, entity);
    }
    
    public ConcurrentHashMap<Long, T> getSnapshots(K key) {
        return snapshotStore.get(key);
    }

    @Configuration
    public static class StoreConfiguration {

        @Bean
        public InMemoryStore<String, LocationDTO> locationStore() {
            return new InMemoryStore<>();
        }

        @Bean
        public InMemoryStore<Integer, CategoryDTO> categoryStore() {
            return new InMemoryStore<>();
        }
    }
}
