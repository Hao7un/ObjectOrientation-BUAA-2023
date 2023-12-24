import com.oocourse.spec1.main.Person;

import java.util.HashMap;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private HashMap<Integer, Person> acquaintance = new HashMap<>(); //id-->Person
    private HashMap<Integer, Integer> values = new HashMap<>();      //id --> value

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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
        return person.getId() == this.id || acquaintance.containsKey(person.getId());
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return values.get(person.getId());
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void addAcquaintance(int value,MyPerson person) {
        acquaintance.put(person.getId(),person);
        values.put(person.getId(),value);
    }

    public HashMap<Integer,Person> getAcquaintance() {
        return acquaintance;
    }
}