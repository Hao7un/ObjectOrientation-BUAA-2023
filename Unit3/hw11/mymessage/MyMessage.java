package mymessage;

import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

public class MyMessage implements Message {
    private final int id;
    private final int socialValue;
    private final int type;
    private final Group group;
    private final Person person1;
    private final Person person2;

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1,
                     Person messagePerson2) {
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.group = null;
        this.type = 0;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
    }

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1,
                     Group messageGroup) {
        this.id = messageId;
        this.person2 = null;
        this.type = 1;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.group = messageGroup;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public Person getPerson1() {
        return person1;
    }

    @Override
    public Person getPerson2() {
        return person2;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Message)) {
            return ((Message) obj).getId() == id;
        }
        return false;
    }
}

