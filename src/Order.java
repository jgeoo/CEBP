import java.util.concurrent.atomic.AtomicLong;

public class Order {
    static final AtomicLong idGenerator = new AtomicLong();
    long orderId;
    String company;
    int quantity;
    double price;
    boolean isBuyOrder;

    public Order(String company, int quantity, double price) {
        this.orderId = idGenerator.incrementAndGet();  // Unique order ID
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order ID=" + orderId + ", Company=" + company + ", Quantity=" + quantity + ", Price=" + price;
    }
}
