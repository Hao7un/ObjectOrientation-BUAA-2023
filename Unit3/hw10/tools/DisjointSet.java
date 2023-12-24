package tools;

import java.util.HashMap;
import java.util.Stack;

public class DisjointSet {
    private final HashMap<Integer,Integer> parent; //id-->parent node
    private final HashMap<Integer,HashMap<Integer,Integer>> accessMap; //用于DFS维护连通性
    private int blockSum; //维护blockSum

    public DisjointSet() {
        parent = new HashMap<>();
        accessMap = new HashMap<>();
        blockSum = 0;
    }

    public void add(int x) {
        blockSum++;
        parent.put(x,x);
        HashMap<Integer,Integer> temp = new HashMap<>();
        accessMap.put(x,temp);
    }

    public int find(int x) {
        if (parent.get(x) == x) {
            return x;
        } else {
            return find(parent.get(x));
        }
    }

    public void merge(int id1, int id2) {
        /*维护accessMap*/
        accessMap.get(id1).put(id2,1);
        accessMap.get(id2).put(id1,1);
        /*维护parent和blockSum*/
        int root1 = find(id1);
        int root2 = find(id2);
        if (root1 == root2) {
            return;
        }
        parent.put(root1,root2);
        blockSum--;
    }

    public int getBlockSum() {
        return this.blockSum;
    }

    public void removeRelation(int id1,int id2) {
        accessMap.get(id1).remove(id2);
        accessMap.get(id2).remove(id1);
        boolean isConnected = dfs(id1,id2); //先判断连通性
        if (!isConnected) { //删除后不连通
            dfs(id2,id1); //更新id2所在连通块的parent
            blockSum++;
        }
    }

    private boolean dfs(int id1,int id2) {
        HashMap<Integer, Boolean> visited = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        boolean result = false;
        for (Integer id : accessMap.keySet()) {
            visited.put(id, false);
        }
        visited.put(id1,true);
        stack.push(id1);
        while (!stack.isEmpty()) {
            int currentNode = stack.peek();
            parent.put(currentNode,id1);
            boolean foundNextNode = false;
            HashMap<Integer, Integer> neighbors = accessMap.get(currentNode);
            for (int neighbor : neighbors.keySet()) {
                if (!visited.get(neighbor)) {
                    visited.put(neighbor,true);
                    stack.push(neighbor);
                    if (neighbor == id2) {
                        result = true;
                    }
                    foundNextNode = true;
                    break;
                }
            }
            if (!foundNextNode) {
                stack.pop();
            }
        }
        return result;
    }

}
