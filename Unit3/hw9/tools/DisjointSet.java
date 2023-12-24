package tools;

import java.util.HashMap;

public class DisjointSet {
    private final HashMap<Integer,Integer> parent; //id-->parent node
    private int blockSum; //维护blockSum

    public DisjointSet() {
        parent = new HashMap<>();
        blockSum = 0;
    }

    public void add(int x) {
        blockSum++;
        parent.put(x,x);
    }

    public int find(int x) {
        if (parent.get(x) == x) {
            return x;
        } else {
            return find(parent.get(x));
        }
    }

    public void merge(int id1, int id2) {
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

}
