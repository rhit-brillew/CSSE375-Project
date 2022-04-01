package risk.model;

import java.util.Random;

public class Edge {

    private String t1;
    private String t2;
    public boolean traversed;

    public Edge(String t1, String t2) {
        this.t1 = t1;
        this.t2 = t2;
        traversed = false;
    }

    public String getT1() {
        return  t1;
    }

    public String getT2() {
        return t2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return traversed == edge.traversed && t1.equals(edge.t1) && t2.equals(edge.t2);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Hashcode not supported");
    }
}
