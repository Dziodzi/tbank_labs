package io.github.dziodzi.service.observer;

import io.github.dziodzi.tools.LogExecutionTime;

import java.util.ArrayList;
import java.util.List;

@LogExecutionTime
public class Publisher {
    private final List<Subscriber> subscribers = new ArrayList<>();
    
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
    
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
    
    protected void notifySubscribers(String message) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(message);
        }
    }
}
