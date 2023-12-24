package utils;

import java.util.Objects;

public class Book {
    private String type;
    private String id;
    private String libraryId;
    private int state; //0代表损坏，1代表正常 , -1代表丢失
    private int borrowedDays;

    public Book(String type,String id,String libraryId,int borrowedDays) {
        this.type = type;
        this.id = id;
        this.state = 1;
        this.libraryId = libraryId;
        this.borrowedDays = borrowedDays;
    }

    public String getLibraryId() {
        return libraryId;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isSmeared() {
        return state == 0;
    }

    public void setSmeared() {
        this.state = 0;
    }

    public boolean isLost() {
        return state == -1;
    }

    public int getBorrowedDays() {
        return borrowedDays;
    }

    public void setBorrowedDays(int borrowedDays) {
        this.borrowedDays =  borrowedDays;
    }

    public void setLost() {
        this.state = -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Book)) {
            return ((Book) obj).getType().equals(type) &&
                    ((Book) obj).getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type,id);
    }

}
