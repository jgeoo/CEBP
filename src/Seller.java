public class Seller implements Runnable {
    private final StockExchange stockExchange;
    private Order order;

    public Seller(StockExchange stockExchange, String company, int quantity, double price) {
        this.stockExchange = stockExchange;
        this.order = new Order(company, quantity, price);
    }

    @Override
    public void run() {
        // Place initial sell order
        stockExchange.placeSellOrder(order);

        // Simulate modification after some time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
