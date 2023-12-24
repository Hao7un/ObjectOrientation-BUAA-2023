import java.util.ArrayList;
import java.util.Objects;

public class Student {
    private String id;
    private ArrayList<Book> borrowedBooks;

    public Student(String id) {
        this.borrowedBooks = new ArrayList<>();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void addBook(Book book) {
        borrowedBooks.add(book);
    }

    public Book getBook(String type, String id) {
        for (Book book: borrowedBooks) {
            if (book.getType().equals(type) && book.getId().equals(id)) {
                return book;
            }
        }
        return null;
    }

    public boolean hasBook(Book book) {
        for (Book book1: borrowedBooks) {
            if (book1.getType().equals(book.getType()) && book1.getId().equals(book.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBType() {
        for (Book book:borrowedBooks) {
            if (book.getType().equals("B")) {
                return true;
            }
        }
        return false;
    }

    public void removeBook(String type, String id) {
        for (Book book:borrowedBooks) {
            if (book.getType().equals(type) && book.getId().equals(id)) {
                borrowedBooks.remove(book);
                break;
            }
        }
    }

    public void smearBook(String type, String id) {
        for (Book book:borrowedBooks) {
            if (book.getType().equals(type) && book.getId().equals(id)) {
                book.setSmeared();
            }
        }
    }

    public void lossBook(String type, String id) {
        for (Book book:borrowedBooks) {
            if (book.getType().equals(type) && book.getId().equals(id)) {
                book.setLost();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Student)) {
            return ((Student) obj).getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
