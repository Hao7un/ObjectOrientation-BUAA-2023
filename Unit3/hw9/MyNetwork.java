import myexception.MyEqualPersonIdException;
import myexception.MyEqualRelationException;
import myexception.MyPersonIdNotFoundException;
import myexception.MyRelationNotFoundException;
import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;
import tools.DisjointSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;
    private final DisjointSet disjointSet;  //used for isCircle and queryBlockSum
    private int triSum;

    public MyNetwork() {
        this.people = new HashMap<>();
        this.disjointSet = new DisjointSet();
        this.triSum = 0;
    }

    @Override
    public boolean contains(int id) {
        return people.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        if (people.containsKey(id)) {
            return people.get(id);
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (people.containsKey(person.getId())) { //网络中已经存在过这个人
            throw new MyEqualPersonIdException(person.getId());
        }
        disjointSet.add(person.getId());
        people.put(person.getId(), person);
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (people.get(id1).isLinked(people.get(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        MyPerson person1 = (MyPerson) people.get(id1);
        MyPerson person2 = (MyPerson) people.get(id2);

        /*维护triSum*/
        int size1 = person1.getAcquaintance().keySet().size();
        int size2 = person2.getAcquaintance().keySet().size();
        int v1 = (size1 <= size2) ? id1 : id2; //v1 为度数更小的结点
        int v2 = (v1 == id1) ? id2 : id1;      //v2 为度数更大的结点
        MyPerson temp = (MyPerson) people.get(v1);
        for (Integer acquaintance : temp.getAcquaintance().keySet()) {
            if (people.get(acquaintance).isLinked(people.get(v2))) {   //acquaintance
                triSum++;
            }
        }

        person1.addAcquaintance(value, person2);
        person2.addAcquaintance(value, person1);
        /*维护disjointSet和blockSum*/
        disjointSet.merge(id1,id2);
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!people.get(id1).isLinked(people.get(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return people.get(id1).queryValue(people.get(id2));
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        return disjointSet.find(id1) == disjointSet.find(id2);
    }

    @Override
    public int queryBlockSum() {
        return disjointSet.getBlockSum();
    }

    @Override
    public int queryTripleSum() {
        return triSum;
    }

    @Override
    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        /*check HashMap*/
        if (beforeData.size() != afterData.size()) { //大小不一致
            return false;
        }
        for (int key1 : beforeData.keySet()) {
            if (!afterData.containsKey(key1)) {     //不包含这个key
                return false;
            }
            HashMap<Integer,Integer> innerBefore = beforeData.get(key1);
            HashMap<Integer,Integer> innerAfter = afterData.get(key1);
            if (innerBefore.size() != innerAfter.size()) { //内层hashmap大小不一致
                return false;
            }
            for (int key2 : innerBefore.keySet()) {    //对内层hashmap的key
                if (!innerAfter.containsKey(key2)) {   //after不包含key2
                    return false;
                }
                if (!innerBefore.get(key2).equals(innerAfter.get(key2))) {
                    return false;
                }
            }
        }

        /*check result according to JML*/
        ArrayList<Person> peopleTest = new ArrayList<>(); //根据JML采用ArrayList的方式装
        Set<Integer> testIdSet = beforeData.keySet();
        for (Integer id: testIdSet) { //对关系中的每一个人
            MyPerson person = new MyPerson(id,"oktest",10);
            for (int acquaintance: beforeData.get(id).keySet()) { //对ta的每一个acquaintance
                MyPerson temp = new MyPerson(acquaintance,"oktest",10);
                person.addAcquaintance(beforeData.get(id).get(acquaintance),temp);
            }
            peopleTest.add(person); //构造完一个人后，加入到ArrayList之中
        }

        int count = 0;
        for (int i = 0; i < peopleTest.size(); i++) {
            for (int j = i + 1; j < peopleTest.size(); j++) {
                for (int k = j + 1; k < peopleTest.size(); k++) {
                    if (peopleTest.get(i).isLinked(peopleTest.get(j)) &&
                        peopleTest.get(i).isLinked(peopleTest.get(k)) &&
                        peopleTest.get(j).isLinked(peopleTest.get(k))) {
                        count++;
                    }
                }
            }
        }

        return result == count;
    }
}