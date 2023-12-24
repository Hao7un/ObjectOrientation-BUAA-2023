package tools;

public class Node implements Comparable<Node> {

    private int id;
    private int dis1;
    private int dis2;
    private   int origin1;
    private int origin2;

    public Node(int id, int dis1, int dis2, int origin1, int origin2) {
        this.id = id;
        this.dis1 = dis1;
        this.dis2 = dis2;
        this.origin1 = origin1;
        this.origin2 = origin2;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(dis1, o.dis1);
    }

    public int getId() {
        return id;
    }

    public int getDis1() {
        return dis1;
    }

    public int getDis2() {
        return dis2;
    }

    public int getOrigin1() {
        return origin1;
    }

    public int getOrigin2() {
        return origin2;
    }

    public void setDis1(int dis1) {
        this.dis1 = dis1;
    }

    public void setDis2(int dis2) {
        this.dis2 = dis2;
    }

    public void setOrigin1(int origin1) {
        this.origin1 = origin1;
    }

    public void setOrigin2(int origin2) {
        this.origin2 = origin2;
    }
}
