package io.p13i.ra.utils;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Simple wrapper around a queue that limits the size of a queue and tracks the last element added to the queue.
 * @param <T> The type of elements in the buffer
 */
class LimitedCapacityBuffer<T> {

    private Queue<T> queue;
    private int totalAddedElementsCount = 0;
    private T lastAddedElement;
    private int maximumSize;

    LimitedCapacityBuffer(int maximumSize) {
        this.maximumSize = maximumSize;
        queue = new LinkedList<T>();
    }

    void add(T element) {
        if (queue.size() >= maximumSize) {
            queue.poll();
        }
        queue.add(element);
        totalAddedElementsCount++;
        lastAddedElement = element;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (T c : queue) {
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    int size() {
        return queue.size();
    }

    T getLastAddedElement() {
        return lastAddedElement;
    }

    int getTotalAddedElementsCount() {
        return totalAddedElementsCount;
    }
}
