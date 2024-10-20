class Seller implements Runnable {
    private StockExchange stockExchange;
    private String company;
    private int quantity;
    private double price;

    public Seller(StockExchange stockExchange, String company, int quantity, double price) {
        this.stockExchange = stockExchange;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public void run() {
            Order order = new Order(company, quantity, price);
            stockExchange.placeSellOrder(order);
    }
}
