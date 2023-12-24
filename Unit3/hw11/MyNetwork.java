import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import myexception.MyPathNotFoundException;
import myexception.MyEqualEmojiIdException;
import myexception.MyEmojiIdNotFoundException;
import myexception.MyAcquaintanceNotFoundException;
import myexception.MyEqualMessageIdException;
import myexception.MyEqualGroupIdException;
import myexception.MyEqualPersonIdException;
import myexception.MyEqualRelationException;
import myexception.MyGroupIdNotFoundException;
import myexception.MyMessageIdNotFoundException;
import myexception.MyPersonIdNotFoundException;
import myexception.MyRelationNotFoundException;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import tools.DisjointSet;
import tools.Node;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class MyNetwork implements Network {
    private final HashMap<Integer, Person> people;
    private final HashMap<Integer, Group> groups = new HashMap<>();
    private final HashMap<Integer, Message> messages = new HashMap<>();
    private final HashMap<Integer,Integer> emojis = new HashMap<>();
    private DisjointSet disjointSet;  //used for isCircle and queryBlockSum
    private int triSum;  //需要动态维护
    private HashMap<Integer,Node> record; //dijkstra record

    public MyNetwork() {
        this.people = new HashMap<>();
        this.disjointSet = new DisjointSet();
        this.triSum = 0;
        this.record = new HashMap<>();
    }

    @Override
    public boolean contains(int id) {
        return people.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        if (people.containsKey(id)) { return people.get(id); }
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
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
        else if (people.get(id1).isLinked(people.get(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        MyPerson person1 = (MyPerson) people.get(id1);
        MyPerson person2 = (MyPerson) people.get(id2);
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
        person1.addAcquaintance(value, person2);
        person2.addAcquaintance(value, person1);
        disjointSet.merge(id1,id2);
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
        if (!contains(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (!contains(id2)) { throw new MyPersonIdNotFoundException(id2); }
        else if (id1 == id2) { throw new MyEqualPersonIdException(id1); }
        else if (!getPerson(id1).isLinked(getPerson(id2))) {
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
            int size1 = ((MyPerson)person1).getAcquaintances().keySet().size(); //维护trisum
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
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
        else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return getPerson(id1).queryValue(getPerson(id2));
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (!people.containsKey(id2)) { throw new MyPersonIdNotFoundException(id2); }
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
        if (!groups.containsKey(group.getId())) { groups.put(group.getId(),group); }
        else { throw new MyEqualGroupIdException(group.getId()); }
    }

    @Override
    public Group getGroup(int id) {
        return groups.get(id);
    }

    @Override
    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) { throw new MyGroupIdNotFoundException(id2); }
        else if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        } else if (this.groups.get(id2).getSize() <= 1111) {
            getGroup(id2).addPerson(getPerson(id1));
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) { throw new MyGroupIdNotFoundException(id); }
        return groups.get(id).getValueSum();
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) { throw new MyGroupIdNotFoundException(id); }
        return groups.get(id).getAgeVar();
    }

    @Override
    public void delFromGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!groups.containsKey(id2)) { throw new MyGroupIdNotFoundException(id2); }
        else if (!people.containsKey(id1)) { throw new MyPersonIdNotFoundException(id1); }
        else if (!getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        groups.get(id2).delPerson(getPerson(id1));
    }

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws EqualMessageIdException,
            EmojiIdNotFoundException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if (message instanceof EmojiMessage &&
                !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
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
        if (!containsMessage(id)) { throw new MyMessageIdNotFoundException(id); }
        else if (getMessage(id).getType() == 0 &&
                !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                                                  getMessage(id).getPerson2().getId());
        } else if (getMessage(id).getType() == 1 &&
                !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }
        if (getMessage(id).getType() == 0) { sendMessageType0(id); }
        else { sendMessageType1(id); }
    }

    public void sendMessageType0(int id) {
        Person person1 = getMessage(id).getPerson1();
        Person person2 = getMessage(id).getPerson2();
        person1.addSocialValue(getMessage(id).getSocialValue());
        person2.addSocialValue(getMessage(id).getSocialValue());
        Message message = getMessage(id);
        if (message instanceof RedEnvelopeMessage) {
            int luckyMoney = ((RedEnvelopeMessage) message).getMoney();
            person1.addMoney(-1 * luckyMoney);
            person2.addMoney(luckyMoney);
        } else if (message instanceof EmojiMessage) {
            int emojiId = ((EmojiMessage) message).getEmojiId();
            int current = emojis.get(emojiId);
            emojis.put(emojiId,current + 1);
        }
        ((MyPerson)person2).addMessage(getMessage(id));  //加入到person2的messages
        messages.remove(id); //从messages中去除
    }

    public void sendMessageType1(int id) {
        Group group = getMessage(id).getGroup();
        ((MyGroup) group).addSocialValue(getMessage(id).getSocialValue());
        Person person1 = getMessage(id).getPerson1();
        Message message = getMessage(id);
        if (message instanceof RedEnvelopeMessage) {
            int i = ((RedEnvelopeMessage)getMessage(id)).getMoney()
                    / getMessage(id).getGroup().getSize();
            person1.addMoney(-i * (getMessage(id).getGroup().getSize() - 1));
            for (Integer pid: people.keySet()) {
                if (message.getGroup().hasPerson(getPerson(pid))) {
                    if (!getPerson(pid).equals(person1)) {
                        getPerson(pid).addMoney(i);
                    }
                }
            }
        } else if (message instanceof EmojiMessage) {
            int emojiId = ((EmojiMessage) message).getEmojiId();
            int current = emojis.get(emojiId);
            emojis.put(emojiId,current + 1);
        }
        messages.remove(id);
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!people.containsKey(id)) { throw new MyPersonIdNotFoundException(id); }
        return getPerson(id).getReceivedMessages();
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojis.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) { throw new MyEqualEmojiIdException(id); }
        emojis.put(id,0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) { throw new MyPersonIdNotFoundException(id); }
        return getPerson(id).getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojis.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        Iterator<Map.Entry<Integer, Integer>> iterator1 = emojis.entrySet().iterator();
        Map.Entry<Integer, Integer> entry1;
        while (iterator1.hasNext()) {
            entry1 = iterator1.next();
            if (entry1.getValue() < limit) {
                iterator1.remove();
            }
        }
        Iterator<Map.Entry<Integer, Message>> iterator2 = messages.entrySet().iterator();
        Map.Entry<Integer, Message> entry2;
        while (iterator2.hasNext()) {
            entry2 = iterator2.next();
            if (entry2.getValue() instanceof EmojiMessage &&
                    !containsEmojiId(((EmojiMessage) entry2.getValue()).getEmojiId())) {
                iterator2.remove();
            }
        }
        return emojis.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        ((MyPerson)getPerson(personId)).clearNotices();
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
            if (((MyPerson)getPerson(id)).getAcquaintances().isEmpty()) { continue; }
            int bestAcquaintance = ((MyPerson)getPerson(id)).getBestAcquaintance();
            if (((MyPerson)getPerson(bestAcquaintance)).getBestAcquaintance() == id) { sum++; }
        }
        return sum >> 1;
    }

    @Override
    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        dijkstra(id);
        int minCircle = Integer.MAX_VALUE;
        for (Integer pid: people.keySet()) {
            if (pid == id) {
                continue;
            }
            if (record.get(pid).getDis1() + record.get(pid).getDis2() < minCircle) {
                minCircle = record.get(pid).getDis1() + record.get(pid).getDis2();
            }
        }
        if (minCircle > Integer.MAX_VALUE / 3) {
            throw  new MyPathNotFoundException(id);
        }
        return minCircle;
    }

    public void dijkstra(int start) { //priority_queue-dijkstra
        boolean result1;
        record = new HashMap<>();
        PriorityQueue<Node>  pq = new PriorityQueue<>();
        for (Integer id : people.keySet()) { //初始化
            if (id != start && getPerson(id).isLinked(getPerson(start))) {
                Node node = new Node(id, getPerson(id).queryValue(getPerson(start)),
                        Integer.MAX_VALUE / 3, id, -1);
                record.put(id, node);
                pq.offer(node);
            } else {
                Node node = new Node(id, Integer.MAX_VALUE / 3, Integer.MAX_VALUE / 3, -1, -1);
                record.put(id, node);
            }
        }
        record.get(start).setDis1(0);
        record.get(start).setDis2(0);
        while (!pq.isEmpty()) {
            Node curr = pq.poll();
            int u = curr.getId();
            int dis1 = curr.getDis1();
            int dis2 = curr.getDis2();
            for (int v: ((MyPerson)getPerson(u)).getAcquaintances().keySet()) {
                result1 = pathUpdate(u,v,dis1);
                pathUpdate(u,v,dis2);
                if (result1) {
                    pq.offer(record.get(v));
                }
            }
        }
    }

    public boolean pathUpdate(int u, int v,int dis) {
        if (dis + getPerson(u).queryValue(getPerson(v)) < record.get(v).getDis1()) {
            if (record.get(u).getOrigin1() != record.get(v).getOrigin1()) {
                record.get(v).setDis2(record.get(v).getDis1());     //把v的最短路赋值给v的次短路
                record.get(v).setOrigin2(record.get(v).getOrigin1());
            }
            record.get(v).setDis1(dis + getPerson(u).queryValue(getPerson(v)));
            record.get(v).setOrigin1(record.get(u).getOrigin1()); //最短路的出点改为新路径的出点
            return true;
        } else if (dis + getPerson(u).queryValue(getPerson(v)) < record.get(v).getDis2()) {
            if (record.get(u).getOrigin1() != record.get(v).getOrigin1()) {
                record.get(v).setDis2(dis + getPerson(u).queryValue(getPerson(v)));
                record.get(v).setOrigin2(record.get(u).getOrigin1());
                return false;
            }
        }
        return false;
    }

    @Override
    public int deleteColdEmojiOKTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        Oktest oktest = new Oktest(limit,beforeData,afterData,result);
        return oktest.test();
    }

}