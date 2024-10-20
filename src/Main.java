//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        StockExchange StockExchange = new StockExchange();

        Thread buyer1 = new Thread(new Buyer(StockExchange, "CompanyA", 20, 55.0));
        Thread buyer2 = new Thread(new Buyer(StockExchange, "CompanyA", 25, 55.0));
        Thread buyer3 = new Thread(new Buyer(StockExchange, "CompanyA", 5, 55.0));

        Thread seller1 = new Thread(new Seller(StockExchange, "CompanyA", 50, 55.0));

        buyer1.start();
        buyer2.start();
        buyer3.start();
        seller1.start();

        while (true) {
            try {
                Thread.sleep(5000);
                StockExchange.displayOrderBook();

                StockExchange.displayTransactions();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}