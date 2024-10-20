class Buyer implements Runnable {
    private StockExchange stockExchange;
    private String company;
    private int quantity;
    private double price;

    public Buyer(StockExchange stockExchange, String company, int quantity, double price) {
        this.stockExchange = stockExchange;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public void run() {
       // while (true) {

            Order order = new Order(company, quantity, price);
            stockExchange.placeBuyOrder(order);

         //   try {
         //       Thread.sleep(3000);  // Pauză între acțiuni (simulează un delay de 3 secunde)
         //   } catch (InterruptedException e) {
         //       e.printStackTrace();
         //   }
       // }
    }
}
