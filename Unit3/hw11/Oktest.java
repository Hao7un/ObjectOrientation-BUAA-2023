import java.util.ArrayList;
import java.util.HashMap;

public class Oktest {
    private int limit;
    private ArrayList<HashMap<Integer, Integer>> beforeData;
    private ArrayList<HashMap<Integer, Integer>> afterData;
    private int result;

    public Oktest(int limit,ArrayList<HashMap<Integer, Integer>> beforeData,
                  ArrayList<HashMap<Integer, Integer>> afterData,int result) {
        this.limit = limit;
        this.beforeData = beforeData;
        this.afterData = afterData;
        this.result = result;
    }

    public int test() {
        HashMap<Integer,Integer> afterEmojis = afterData.get(0);      //HashMap<emojiId, emojiHeat>
        HashMap<Integer,Integer> beforeEmojis = beforeData.get(0);    //HashMap<emojiId, emojiHeat>
        for (Integer emojiId : beforeEmojis.keySet()) {
            if (beforeEmojis.get(emojiId) >= limit &&
                    !afterEmojis.containsKey(emojiId)) {
                return 1;
            }
        }
        for (Integer emojiId : afterEmojis.keySet()) {
            if (!(beforeEmojis.containsKey(emojiId) &&
                    beforeEmojis.get(emojiId).equals(afterEmojis.get(emojiId)))) { return 2; }
        }
        int number = 0;
        for (Integer emojiId : beforeEmojis.keySet()) {
            if (beforeEmojis.get(emojiId) >= limit) {
                number++;
            }
        }
        if (number != afterEmojis.size()) {
            return 3;
        }
        HashMap<Integer,Integer> afterMessages = afterData.get(1);    //HashMap<messageId, emojiId>
        HashMap<Integer,Integer> beforeMessages = beforeData.get(1);  //HashMap<messageId, emojiId>
        for (Integer messageId : beforeMessages.keySet()) {
            if (beforeMessages.get(messageId) != null &&
                    afterEmojis.containsKey(beforeMessages.get(messageId))) { //condition
                if (!beforeMessages.get(messageId).equals(afterMessages.get(messageId))) {
                    return 5;
                }
                if (!afterMessages.containsKey(messageId)) {
                    return 5;
                }
            }
        }
        for (Integer messageId : beforeMessages.keySet()) {
            if (beforeMessages.get(messageId) == null) { //not instance of emojiMessage
                if (afterMessages.get(messageId) != null) {
                    return 6;
                }
                if (!afterMessages.containsKey(messageId)) {
                    return 6;
                }
            }
        }
        number = 0;
        for (Integer messageId : beforeMessages.keySet()) {
            if ((beforeMessages.get(messageId) != null &&
                    afterEmojis.containsKey(beforeMessages.get(messageId)))
                    || (beforeMessages.get(messageId) == null)) {
                number++;
            }
        }
        if (number != afterMessages.size()) {
            return 7;
        }
        if (result != afterEmojis.size()) { return 8; }
        return 0;
    }
}
