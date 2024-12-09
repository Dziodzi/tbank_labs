package io.github.dziodzi.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Snapshot<T> {
    private final T state;
    private final long timestamp;
    
    public Snapshot(T state) {
        this.state = deepCopy(state);
        this.timestamp = System.currentTimeMillis();
    }
    
    public T getState() {
        return state;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    private T deepCopy(T original) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return (T) mapper.readValue(mapper.writeValueAsString(original), original.getClass());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create a deep copy", e);
        }
    }
}
