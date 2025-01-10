package io.github.dziodzi.repository;

import io.github.dziodzi.entity.Snapshot;
import io.github.dziodzi.entity.dto.CategoryDTO;
import io.github.dziodzi.entity.dto.LocationDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore<K, T> {
    private final ConcurrentHashMap<K, T> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, ConcurrentHashMap<Long, Snapshot<T>>> snapshotStore = new ConcurrentHashMap<>();

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
        T currentEntity = store.get(key);
        if (currentEntity != null) {
            createSnapshot(key, currentEntity); // Создаём снимок перед обновлением
        }
        store.put(key, entity);
    }

    public void delete(K key) {
        store.remove(key);
        snapshotStore.remove(key);
    }

    private void createSnapshot(K key, T entity) {
        Snapshot<T> snapshot = new Snapshot<>(entity);
        snapshotStore.putIfAbsent(key, new ConcurrentHashMap<>());
        snapshotStore.get(key).put(snapshot.getTimestamp(), snapshot);
    }
    
    public Collection<Snapshot<T>> getSnapshots(K key) {
        var snapshots = snapshotStore.get(key);
        if (snapshots == null) {
            return new ArrayList<>();
        }
        return snapshots.values();
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
