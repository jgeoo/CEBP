// Transaction class representing a completed trade between buyers and sellers
class Transaction {
    String company;     // Company whose shares are being traded
    int quantity;       // Number of shares involved in the transaction
    double price;       // Price per share in the transaction

    // Constructor initializes the company, quantity, and price of the transaction
    public Transaction(String company, int quantity, double price) {
        this.company = company;
        this.quantity = quantity;
        this.price = price;
    }

    // toString method provides a readable representation of the transaction details
    public String toString() {
        return "Company: " + company + ", Quantity: " + quantity + ", Price: " + price;
    }
}