package sti.globals;

public class Product {
    private int id;
    private String category;
    private String discount;

    public Product(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }

    @Override
    public String toString() {
        return "Product{id='" + id + "', category='" + category + "', discount='" + discount + "'}";
    }
}
