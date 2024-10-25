import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// The OrderLockManager class provides a locking mechanism for individual orders,
// allowing concurrency control for orders without locking the entire stock exchange.
// This approach is designed to optimize performance by ensuring that locks are
// only applied to specific orders, reducing the risk of bottlenecks.
public class OrderLockManager {

    // ConcurrentHashMap to store ReentrantLocks associated with each order ID.
    // Using ConcurrentHashMap ensures thread safety when accessing or modifying locks,
    // which is essential for concurrent environments where multiple orders are processed simultaneously.
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    // Retrieves or creates a ReentrantLock for a specific order ID.
    // The computeIfAbsent method either returns the existing lock for the order ID
    // or creates a new ReentrantLock if the order ID isn't already in the map.
    public ReentrantLock getLock(long orderId) {
        return lockMap.computeIfAbsent(orderId, id -> new ReentrantLock());
    }

    // Releases the lock associated with a specific order ID.
    // This method checks if the lock exists and is no longer held by any thread.
    // If the lock's hold count is 1 (meaning it's only held by one thread),
    // it is removed from the map to free resources and allow garbage collection.
    public void releaseLock(long orderId) {
        ReentrantLock lock = lockMap.get(orderId);

        // Check if the lock exists and is only held by one thread before removing it.
        // lock.getHoldCount() == 1 ensures that no other threads are waiting on this lock,
        // which prevents prematurely removing locks that are still in use.
        if (lock != null && lock.getHoldCount() == 1) {
            lockMap.remove(orderId);  // Remove lock from map, as it's no longer needed.
        }
    }
}
