package utils;

public class Request {
    private Integer year;
    private Integer month;
    private Integer day;
    private Student student;
    private Book book;

    public Request(Integer year,Integer month,Integer day,Student student,Book book) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.student = student;
        this.book = book;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public Student getStudent() {
        return student;
    }

    public Book getBook() {
        return book;
    }

}
