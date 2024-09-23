package io.github.dziodzi.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

@Component
public class InMemoryStore<T> {
    private final ConcurrentHashMap<Integer, T> store = new ConcurrentHashMap<>();
    private int idCounter = 0;

    public Collection<T> getAll() {
        return store.values();
    }

    public T getById(int id) {
        return store.get(id);
    }

    public T create(T entity) {
        int id = ++idCounter;
        store.put(id, entity);
        return entity;
    }

    public T update(int id, T entity) {
        store.put(id, entity);
        return entity;
    }

    public void delete(int id) {
        store.remove(id);
    }
}
