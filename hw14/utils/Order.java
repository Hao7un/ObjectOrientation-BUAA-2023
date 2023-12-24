package utils;

public class Order {
    private String sid;
    private Book book;

    private String bookSchool; //书籍所在的学校
    private String studentSchool; //学生所在的学校

    public Order(String sid, Book book,String bookSchool,String studentSchool) {
        this.sid = sid;
        this.book = book;
        this.bookSchool = bookSchool;
        this.studentSchool = studentSchool;
    }

    public String getSid() {
        return sid;
    }

    public Book getBook() {
        return book;
    }

    public String getBookSchool() {
        return bookSchool;
    }

    public String getStudentSchool() {
        return studentSchool;
    }
}
