public class Main {
    public static void main(String[] args) {
        StockExchange stockExchange = new StockExchange();

        // Create buyer and seller threads
        Thread buyer1 = new Thread(new Buyer(stockExchange, "TechCorp", 25, 55.0));
        Thread buyer2 = new Thread(new Buyer(stockExchange, "HealthInc", 23, 55.0));
        Thread buyer3 = new Thread(new Buyer(stockExchange, "FinServ", 15, 54.5));
        Thread buyer4 = new Thread(new Buyer(stockExchange, "EnergySolutions", 30, 56.0));
        Thread buyer5 = new Thread(new Buyer(stockExchange, "EnergySolutions", 30, 56.0));

        Thread seller1 = new Thread(new Seller(stockExchange, "TechCorp", 50, 55.0));
        Thread seller2 = new Thread(new Seller(stockExchange, "HealthInc", 10, 54.0));
        Thread seller3 = new Thread(new Seller(stockExchange, "FinServ", 20, 55.0));
        Thread seller4 = new Thread(new Seller(stockExchange, "EnergySolutions", 40, 56.0));

        // Start buyers and sellers
        buyer1.start();
        buyer2.start();
        buyer3.start();
        buyer4.start();
        buyer5.start();
        seller1.start();
        seller2.start();
        seller3.start();
        seller4.start();
        //        stockExchange.stopMatcher(); if the stockexchange reaches 12:00 or closing hours for stock we stop it with this <
    }
}
