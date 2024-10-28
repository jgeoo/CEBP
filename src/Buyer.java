public class Buyer implements Runnable {
    private final StockExchange stockExchange;
    private Order order;

    public Buyer(StockExchange stockExchange, String company, int quantity, double price) {
        this.stockExchange = stockExchange;
        this.order = new Order(company, quantity, price);
    }

    @Override
    public void run() {
        // Place initial buy order
        stockExchange.placeBuyOrder(order);

        // Simulate modification after some time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
    public void modifyOrder(String company,int newQuantity, double newPrice) {
        Order updatedOrder = new Order(company, newQuantity, newPrice);
        stockExchange.updateOrder(updatedOrder);
        System.out.println("Modified Sell Order for " + company + " to quantity: " + newQuantity + ", price: " + newPrice);
    }
}