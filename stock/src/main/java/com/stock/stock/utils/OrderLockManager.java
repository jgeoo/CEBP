package com.stock.stock.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class OrderLockManager {

    // A thread-safe map to store locks associated with each order ID
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    /**
     * Get the lock for a specific order ID. If the lock doesn't exist, it creates one.
     *
     * @param orderId The ID of the order to lock.
     * @return The ReentrantLock associated with the order ID.
     */
    public ReentrantLock getLock(Long orderId) {
        return lockMap.computeIfAbsent(orderId, id -> new ReentrantLock());
    }

    /**
     * Release the lock for a specific order ID. If the lock is no longer held,
     * it removes the lock from the map.
     *
     * @param orderId The ID of the order to unlock.
     */
    public void releaseLock(Long orderId) {
        ReentrantLock lock = lockMap.get(orderId);
        if (lock != null && lock.tryLock()) {
            try {
                lockMap.remove(orderId); // Remove the lock if no threads are waiting on it
            } finally {
                lock.unlock();
            }
        }
    }
}
