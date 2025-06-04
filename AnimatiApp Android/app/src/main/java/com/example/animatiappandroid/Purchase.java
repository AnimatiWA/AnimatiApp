public class Purchase {
    private String date;
    private String items;
    private String total;

    public Purchase(String date, String items, String total) {
        this.date = date;
        this.items = items;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getItems() {
        return items;
    }

    public String getTotal() {
        return total;
    }
}
