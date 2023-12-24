public class Request {
    private String year;
    private String month;
    private String day;
    private Student student;
    private String bid;
    private String type;

    public Request(String year,String month,String day,Student student,String type,String bid) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.student = student;
        this.bid = bid;
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public Student getStudent() {
        return student;
    }

    public String getType() {
        return type;
    }

    public String getBid() {
        return bid;
    }

}
