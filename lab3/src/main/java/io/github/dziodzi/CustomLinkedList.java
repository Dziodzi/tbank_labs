package io.github.dziodzi;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> prev;

        Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T data) {
        Node<T> node = new Node<>(data);
        if (this.head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        size++;
    }

    public T get(int index) {
        return getNode(index).data;
    }

    public void remove(int index) {
        Node<T> current = getNode(index);

        if (current == head) {
            head = head.next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else if (current == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            Node<T> prevNode = current.prev;
            Node<T> nextNode = current.next;
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        size--;
    }

    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void addAll(List<T> list) {
        for (T data : list) {
            add(data);
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        Node<T> current = head;
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (current != null) {
            stringBuilder.append(index).append(": ").append(current.data).append("\n");
            current = current.next;
            index++;
        }
        return stringBuilder.toString();
    }

    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException("CustomLinkedList index is out of range: " + index);
        }

        Node<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }
    
    public CustomIterator<T> iterator() {
        return new CustomLinkedListIterator();
    }
    
    private class CustomLinkedListIterator implements CustomIterator<T> {
        private Node<T> current = head;
        
        public boolean hasNext() {
            return current != null;
        }
        
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
        
        public void forEachRemaining(Consumer<? super T> action) {
            while(hasNext()) {
                action.accept(next());
            }
        }
    }
    
}