import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualMessageIdException;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.exceptions.MessageIdNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import myexception.MyAcquaintanceNotFoundException;
import myexception.MyEqualMessageIdException;
import myexception.MyEqualGroupIdException;
import myexception.MyEqualPersonIdException;
import myexception.MyEqualRelationException;
import myexception.MyGroupIdNotFoundException;
import myexception.MyMessageIdNotFoundException;
import myexception.MyPersonIdNotFoundException;
import myexception.MyRelationNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import tools.DisjointSet;

import java.util.HashMap;
import java.util.List;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;
    private final HashMap<Integer, Group> groups = new HashMap<>();
    private final HashMap<Integer, Message> messages = new HashMap<>();
    private  DisjointSet disjointSet;  //used for isCircle and queryBlockSum
    private int triSum;  //需要动态维护

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
        int size1 = person1.getAcquaintances().keySet().size();
        int size2 = person2.getAcquaintances().keySet().size();
        int v1 = (size1 <= size2) ? id1 : id2; //v1 为度数更小的结点
        int v2 = (v1 == id1) ? id2 : id1;      //v2 为度数更大的结点
        MyPerson temp = (MyPerson) people.get(v1);
        for (Integer acquaintance : temp.getAcquaintances().keySet()) {
            if (people.get(acquaintance).isLinked(people.get(v2))) {   //acquaintance
                triSum++;
            }
        }
        /*维护people's acquaintance*/
        person1.addAcquaintance(value, person2);
        person2.addAcquaintance(value, person1);
        /*维护disjointSet和blockSum*/
        disjointSet.merge(id1,id2);
        /*维护groups*/
        for (Integer groupId: groups.keySet()) {
            if (groups.get(groupId).hasPerson(getPerson(id1)) &&
                groups.get(groupId).hasPerson(getPerson(id2))) {
                ((MyGroup)groups.get(groupId)).addRelation(value);
            }
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1,id2);
        }
        Person person1 = getPerson(id1);
        Person person2 = getPerson(id2);
        if (getPerson(id1).queryValue(getPerson(id2)) + value > 0) {
            for (Integer groupId: groups.keySet()) {            /*维护groups的值*/
                Group group = groups.get(groupId);
                if (group.hasPerson(getPerson(id1)) &&  group.hasPerson(getPerson(id2))) {
                    ((MyGroup)group).updateValueSum(id1,id2,value);
                }
            }
            ((MyPerson)person1).modifyValue(id2,value);            /*维护person的值*/
            ((MyPerson)person2).modifyValue(id1,value);
            ((MyPerson)person1).updateBestAcquaintance();
            ((MyPerson)person2).updateBestAcquaintance();
        } else {
            for (Integer groupId: groups.keySet()) {            /*维护group的valueSum*/
                Group group = groups.get(groupId);
                if (group.hasPerson(getPerson(id1)) && group.hasPerson(getPerson(id2))) {
                    ((MyGroup)group).updateValueSum(id1,id2,value);
                }
            }
            ((MyPerson)person1).modifyValue(id2,value);            /*维护person的值*/
            ((MyPerson)person2).modifyValue(id1,value);
            ((MyPerson)person1).removeAcquaintance(getPerson(id2));
            ((MyPerson)person2).removeAcquaintance(getPerson(id1));
            ((MyPerson)person1).updateBestAcquaintance();
            ((MyPerson)person2).updateBestAcquaintance();
            disjointSet.removeRelation(id1,id2);            /*维护disjointSet and blockSum*/
            /*维护triSum*/
            int size1 = ((MyPerson)person1).getAcquaintances().keySet().size();
            int size2 = ((MyPerson)person2).getAcquaintances().keySet().size();
            int v1 = (size1 <= size2) ? id1 : id2; //v1 为度数更小的结点
            int v2 = (v1 == id1) ? id2 : id1;      //v2 为度数更大的结点
            Person temp = getPerson(v1);
            for (Integer acquaintance : ((MyPerson)temp).getAcquaintances().keySet()) {
                if (getPerson(acquaintance).isLinked(getPerson(v2))) {
                    triSum--;
                }
            }
        }
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!people.containsKey(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return getPerson(id1).queryValue(getPerson(id2));
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
    public void addGroup(Group group) throws EqualGroupIdException {
        if (!groups.containsKey(group.getId())) {
            groups.put(group.getId(),group);
        } else {
            throw new MyEqualGroupIdException(group.getId());
        }
    }

    @Override
    public Group getGroup(int id) {
        return groups.get(id);
    }

    @Override
    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        } else if (this.groups.get(id2).getSize() <= 1111) {
            getGroup(id2).addPerson(getPerson(id1));
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getValueSum();
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return groups.get(id).getAgeVar();
    }

    @Override
    public void delFromGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (!people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        groups.get(id2).delPerson(getPerson(id1));
    }

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws EqualMessageIdException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if (message.getType() == 0 && message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messages.put(message.getId(),message);
    }

    @Override
    public Message getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public void sendMessage(int id) throws RelationNotFoundException,
            MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else if (getMessage(id).getType() == 0 &&
                !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                                                  getMessage(id).getPerson2().getId());
        } else if (getMessage(id).getType() == 1 &&
                !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }
        if (getMessage(id).getType() == 0) {
            Person person1 = getMessage(id).getPerson1();
            Person person2 = getMessage(id).getPerson2();
            person1.addSocialValue(getMessage(id).getSocialValue());
            person2.addSocialValue(getMessage(id).getSocialValue());
            ((MyPerson)person2).addMessage(getMessage(id));  //加入到person2的messages
            messages.remove(id); //从messages中去除
        } else {
            Group group = getMessage(id).getGroup();
            ((MyGroup) group).addSocialValue(getMessage(id).getSocialValue());
            messages.remove(id);
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getReceivedMessages();
    }

    @Override
    public int queryBestAcquaintance(int id) throws PersonIdNotFoundException,
            AcquaintanceNotFoundException {
        if (!people.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else if (((MyPerson)getPerson(id)).getAcquaintances().size() == 0) {
            throw new MyAcquaintanceNotFoundException(id);
        }
        return ((MyPerson)getPerson(id)).getBestAcquaintance();
    }

    @Override
    public int queryCoupleSum() {
        int sum = 0;
        for (Integer id: people.keySet()) {
            if (((MyPerson)getPerson(id)).getAcquaintances().isEmpty()) { //no acquaintance
                continue;
            }
            int bestAcquaintance = ((MyPerson)getPerson(id)).getBestAcquaintance();
            if (((MyPerson)getPerson(bestAcquaintance)).getBestAcquaintance() == id) {
                sum++;
            }
        }
        return sum >> 1;
    }

    @Override
    public int modifyRelationOKTest(int id1, int id2, int value,
                                    HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                    HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        /*check exception*/
        if (!beforeData.containsKey(id1) || !beforeData.containsKey(id2) ||
                (id1 == id2) || !beforeData.get(id1).containsKey(id2)) {
            if (!beforeData.equals(afterData)) { //状态发生改变
                return -1;
            } else { //状态未发生变化
                return 0;
            }
        }
        if (beforeData.get(id1).get(id2) + value > 0) {
            return checkBehaviour1(id1,id2,value,beforeData,afterData);
        } else {
            return checkBehaviour2(id1,id2,value,beforeData,afterData);
        }
    }

    public int checkBehaviour1(int id1, int id2, int value,
                               HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                               HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        /*ensures 1*/
        if (beforeData.keySet().size() != afterData.keySet().size()) { return 1; }
        /*ensures 2*/
        for (Integer id:beforeData.keySet()) {
            if (!afterData.containsKey(id)) { return 2; }
        }
        /*ensures 3*/
        for (Integer id : afterData.keySet()) {
            if (id != id1 && id != id2 && (!beforeData.get(id).equals(afterData.get(id)))) {
                return 3;
            }
        }
        /*ensures 4*/
        if (!(afterData.get(id1).containsKey(id2) && afterData.get(id2).containsKey(id1))) {
            return 4;
        }
        /*ensures 5*/
        if (afterData.get(id1).get(id2) != beforeData.get(id1).get(id2) + value) { return 5; }
        /*ensures 6*/
        if (afterData.get(id2).get(id1) != beforeData.get(id2).get(id1) + value) { return 6; }
        /*ensures 7*/
        if (afterData.get(id1).keySet().size() != beforeData.get(id1).keySet().size()) { return 7; }
        /*ensures 8*/
        if (afterData.get(id2).keySet().size() != beforeData.get(id2).keySet().size()) { return 8; }
        /*ensure 9*/
        if (!afterData.get(id1).keySet().equals(beforeData.get(id1).keySet())) { return 9; }
        /*ensures 10*/
        if (!afterData.get(id2).keySet().equals(beforeData.get(id2).keySet())) { return 10; }
        /*ensures 11*/
        for (Integer id: afterData.get(id1).keySet()) {
            if (id != id2 && (afterData.get(id1).get(id) != beforeData.get(id1).get(id))) {
                return 11;
            }
        }
        /*ensures 12*/
        for (Integer id: afterData.get(id2).keySet()) {
            if (id != id1 && (afterData.get(id2).get(id) != beforeData.get(id2).get(id))) {
                return 12;
            }
        }
        /*ensures 13*/
        /*ensures 14*/
        return 0;
    }

    public int checkBehaviour2(int id1, int id2, int value,
                               HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                               HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        /*ensures 1*/
        if (beforeData.keySet().size() != afterData.keySet().size()) { return 1; }
        /*ensures 2*/
        for (Integer id:beforeData.keySet()) {
            if (!afterData.containsKey(id)) { return 2; }
        }
        /*ensures 3*/
        for (Integer id : afterData.keySet()) {
            if (id != id1 && id != id2 && (!beforeData.get(id).equals(afterData.get(id)))) {
                return 3;
            }
        }
        /*ensures 15*/
        if (afterData.get(id1).containsKey(id2) || afterData.get(id2).containsKey(id1)) {
            return 15;
        }
        /*ensures 16*/
        if (beforeData.get(id1).size() != afterData.get(id1).keySet().size() + 1) { return 16; }
        /*ensures 17*/
        if (beforeData.get(id2).size() != afterData.get(id2).keySet().size() + 1) { return 17; }
        /*ensures 18*/
        /*ensures 19*/
        /*ensures 20*/
        for (Integer id: afterData.get(id1).keySet()) {
            if (!beforeData.get(id1).containsKey(id) ||
                    (beforeData.get(id1).get(id) != afterData.get(id1).get(id))) { return 20; }
        }
        /*ensures 21*/
        for (Integer id: afterData.get(id2).keySet()) {
            if (!beforeData.get(id2).containsKey(id) ||
                    (beforeData.get(id2).get(id) != afterData.get(id2).get(id))) { return 21; }
        }
        return 0;
    }
}