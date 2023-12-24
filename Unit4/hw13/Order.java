public class Order {
    private String sid;
    private Book book;

    public Order(String sid, Book book) {
        this.sid = sid;
        this.book = book;
    }

    public String getSid() {
        return sid;
    }

    public Book getBook() {
        return book;
    }
}
