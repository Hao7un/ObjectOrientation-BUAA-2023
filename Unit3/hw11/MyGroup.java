import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private final int id;
    private final HashMap<Integer, Person> people;
    private int valueSum;
    private int ageSum;

    public MyGroup(int id) {
        this.id = id;
        this.people = new HashMap<>();
        this.valueSum = 0;
        this.ageSum = 0;
    }

    public int getId() {
        return this.id;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof Group)) {
            return ((Group) obj).getId() == id;
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        people.put(person.getId(),person);
        /*维护valueSum*/
        for (Integer member: people.keySet()) {
            valueSum += people.get(member).queryValue(person);
        }
        /*维护ageSum*/
        ageSum += person.getAge();
    }

    public void addSocialValue(int socialValue) {
        for (Integer id : people.keySet()) {
            people.get(id).addSocialValue(socialValue);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return valueSum << 1; /*注意JML描述，相当于返回值要算两遍*/
    }

    public void updateValueSum(int id1,int id2,int value) {
        int currentValue = people.get(id1).queryValue(people.get(id2)); //两人目前的值
        if (currentValue + value > 0) {
            valueSum += value;
        } else { //爆了，那就只能减这么多
            valueSum -= currentValue;
        }
    }

    @Override
    public int getAgeMean() {
        if (people.isEmpty()) {
            return 0;
        } else {
            return ageSum / people.size();
        }
    }

    @Override
    public int getAgeVar() {
        if (people.isEmpty()) {
            return 0;
        } else {
            int mean = getAgeMean();
            int result = 0;
            for (Integer id: people.keySet()) {
                result += (people.get(id).getAge() - mean) * (people.get(id).getAge() - mean);
            }
            return result / people.size();
        }
    }

    @Override
    public void delPerson(Person person) {
        people.remove(person.getId());
        /*维护valueSum*/
        for (Integer member: people.keySet()) {
            valueSum -= people.get(member).queryValue(person);
        }
        /*维护ageSum*/
        ageSum -= person.getAge();
    }

    public void addRelation(int value) {
        valueSum += value;
    }

    @Override
    public int getSize() {
        return people.size();
    }

}

