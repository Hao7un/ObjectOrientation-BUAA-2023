import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Person;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private int socialValue;
    private int bestAcquaintance;
    private int bestAcquaintanceValue;

    private final HashMap<Integer, Person> acquaintances = new HashMap<>(); //id-->Person
    private final HashMap<Integer, Integer> values = new HashMap<>();      //id --> value
    private final LinkedList<Message> messages = new LinkedList<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.socialValue = 0;
        this.bestAcquaintance = 0;
        this.bestAcquaintanceValue = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Person)) {
            return ((Person) obj).getId() == id;
        }
        return false;
    }

    @Override
    public boolean isLinked(Person person) {
        return person.getId() == this.id || acquaintances.containsKey(person.getId());
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintances.containsKey(person.getId())) {
            return values.get(person.getId());
        } else {
            return 0;
        }
    }

    @Override
    public void addSocialValue(int num) {
        socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    public void addMessage(Message message) {
        messages.addFirst(message);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        if (messages.size() < 5) {
            return messages;
        } else {
            return messages.subList(0,5);
        }
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void addAcquaintance(int value,MyPerson person) {
        /*维护bestAcquaintance*/
        if (value > bestAcquaintanceValue ||
                (value == bestAcquaintanceValue && person.getId() < bestAcquaintance)) {
            bestAcquaintanceValue = value;
            bestAcquaintance = person.getId();
        }
        acquaintances.put(person.getId(),person);
        values.put(person.getId(),value);
    }

    public void removeAcquaintance(Person person) {
        acquaintances.remove(person.getId());
    }

    public void updateBestAcquaintance() {
        if (acquaintances.isEmpty()) {
            bestAcquaintance = 0;
            bestAcquaintanceValue = 0;
        }
        boolean flag = true;
        for (Integer id: acquaintances.keySet()) {
            if (flag) {
                flag = false;
                bestAcquaintance = id;
                bestAcquaintanceValue = queryValue(acquaintances.get(id));
                continue;
            }
            if (queryValue(acquaintances.get(id)) > bestAcquaintanceValue ||
                    (queryValue(acquaintances.get(id)) == bestAcquaintanceValue
                            && id < bestAcquaintance)) {
                bestAcquaintance = id;
                bestAcquaintanceValue = queryValue(acquaintances.get(id));
            }
        }
    }

    public HashMap<Integer,Person> getAcquaintances() {
        return acquaintances;
    }

    public int getBestAcquaintance() {
        return bestAcquaintance;
    }

    public void modifyValue(int other,int value) {
        int currentValue = values.get(other);
        if (currentValue + value > 0) {
            values.put(other,currentValue + value);
        } else {
            values.remove(other);
        }
    }
}