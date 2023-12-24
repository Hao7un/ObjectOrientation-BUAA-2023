import java.util.HashMap;

public class LogisticsDivision {
    private HashMap<Book,Integer> books;  //暂存的书-->数量

    public LogisticsDivision(HashMap<Book,Integer> bookShelf) {
        this.books = new HashMap<>();
        for (Book book:bookShelf.keySet()) {
            books.put(book,0);
        }
    }

    public void repairBook(Request request) {
        String type = request.getType();
        String bid = request.getBid();
        Book book = new Book(type,bid); //进行维修
        books.put(book,books.get(book) + 1);
        System.out.println("[" + request.getYear() + "-" + request.getMonth() +
                "-" + request.getDay() + "] " +
                type + "-" + bid + " got repaired by logistics division");
    }

    public HashMap<Book,Integer> getAndclearBooks() {
        HashMap<Book,Integer> temp = new HashMap<>();
        for (Book book:books.keySet()) {
            temp.put(book,books.get(book));
        }
        books.replaceAll((b, v) -> 0);
        return temp;
    }
}
