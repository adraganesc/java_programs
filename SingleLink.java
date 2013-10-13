
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Alex
 */
public class SingleLink {

    private Pair min = new Pair(1, 3);
    private List<Point> points = new ArrayList<>();
    private Cluster[] clusters;

    public static void main(String args[]) {
        SingleLink s = new SingleLink();
        s.addPoint(19, 48);
        s.addPoint(4, 1);
        s.addPoint(21, 7);
        s.addPoint(-12, 8);
        s.addPoint(-13, 19);
        s.addPoint(-41, 21);
        System.out.println(Arrays.toString(s.clusters(2)));
    }
    private double[][] a;

    public void addPoint(double x, double y) {
        points.add(new Point(x, y));
    }

    private void initialize() {
        double min = Double.MIN_VALUE;
        a = table(points.size());
        clusters = new Cluster[points.size()];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < i; j++) {
                double d = points.get(i).getDisimilarity(points.get(j));
                a[i][j] = d;
                if (min >= d) {
                    this.min = new Pair(i, j);
                    min = d;
                }
            }
            this.clusters[i] = new SingleLinkCluster(new int[]{i}, i);
        }
    }

    public Cluster[] clusters(int number) {
        
        initialize();

        do {
            disimilarities(new TreeSet<Cluster>(asList(clusters)));
        } while (clusters.length > number);

        return clusters;
    }

    //to do : make object oriented whit no a as a parameter
    private void disimilarities(Set<Cluster> clusters) {

        int[] ordered = sorted(min.i, min.j);
        SingleLinkCluster[] mergeableClusters = remove(clusters, ordered[0], ordered[1]);

        //the new table
        double[][] d = table(a.length - 1);

        SingleLinkClusterMerger cf = new SingleLinkClusterMerger(mergeableClusters[0], mergeableClusters[1], d.length);
        clusters.add(cf);
        Cluster[] cls = (Cluster[]) clusters.toArray(new Cluster[clusters.size()]);

        double min = Double.MIN_VALUE;

        for (int i = 0; i < cls.length; i++) {
            for (int j = 0; j < i; j++) {//cu <= atingea zerourile
                double v = cls[i].getDisimilarity(cls[j]);
                d[i][j] = v;
                if (min >= v) {
                    this.min = new Pair(i, j);
                    min = v;
                }
            }
        }

        for (int i = 0; i < a.length; i++) {
            a[i] = null;
        }

        clusters.remove(cf);
        clusters.add(cf.cluster());

        this.a = d;

    }

    private int[] sorted(int a, int b) {
        if (a < b) {
            return new int[]{a, b};
        }
        return new int[]{b, a};

    }

    private int[] sortedReverse(int a, int b) {
        if (a > b) {
            return new int[]{a, b};
        }
        return new int[]{b, a};

    }

    private SingleLinkCluster[] remove(Set<Cluster> clusters, int i, int j) {
        Iterator<Cluster> it = clusters.iterator();
        SingleLinkCluster[] result = new SingleLinkCluster[2];
        int l = 0;
        while (it.hasNext()) {
            SingleLinkCluster c = (SingleLinkCluster) it.next();
            if (c.i == i || c.i == j) {
                result[l++] = c;
                continue;
            }
        }
        clusters.removeAll(asList(result));
        return result;
    }

    private double[][] table(int length) {
        double[][] b = new double[length][];

        for (int i = 0; i < b.length; i++) {
            b[i] = new double[i + 1];
        }
        return b;
    }

    private int[] merge(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, c.length - a.length);
        return c;
    }

    private class Point {

        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getDisimilarity(Point other) {
            return (this.x - other.x) * (this.x - other.x)
                    + (this.y - other.y) * (this.y - other.y);
        }
    }

    private class Pair {

        private int i;
        private int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private class SingleLinkClusterMerger implements Cluster, Comparable<Cluster> {

        private SingleLinkCluster c1;
        private SingleLinkCluster c2;
        private int i;

        public SingleLinkClusterMerger(SingleLinkCluster c1, SingleLinkCluster c2, int i) {
            this.c1 = c1;
            this.c2 = c2;
            this.i = i;
        }

        private SingleLinkCluster cluster() {
            SingleLinkCluster c = new SingleLinkCluster();
            c.elements = merge(c1.elements, c2.elements);
            c.i = this.i;
            return c;
        }

        public double getDisimilarity(Cluster other) {
            int[] b1 = sortedReverse(other.getPosition(), c1.i), b2 = sortedReverse(other.getPosition(), c2.i);
            double result = Math.min(a[b1[0]][b1[1]], a[b2[0]][b2[1]]);
            //la mishto
            a[b1[0]][b1[1]] = 0.44545;
            a[b2[0]][b2[1]] = 0.5242;
            return result;
        }

        @Override
        public int compareTo(Cluster o) {
            return this.getPosition() - o.getPosition();
        }

        @Override
        public int getPosition() {
            return this.i;
        }
    }

    public interface Cluster {

        double getDisimilarity(Cluster other);

        int getPosition();
    }

    private class SingleLinkCluster implements Comparable<Cluster>, Cluster {

        private int[] elements;
        private int i;
        private double tmp;

        public SingleLinkCluster() {
        }

        public SingleLinkCluster(int[] elements, int i) {
            this.elements = elements;
            this.i = i;
        }

        public double getDisimilarity(Cluster other) {
            double result = a[this.i][other.getPosition()];
            a[this.i][other.getPosition()] = 0.13424;
            return result;
        }

        @Override
        public int compareTo(Cluster other) {
            return this.getPosition() - other.getPosition();
        }

        @Override
        public String toString() {
            return "Cluster(" + "elements=" + Arrays.toString(elements)
                    + ", index=" + i + ", tmp=" + tmp + ")";
        }

        @Override
        public int getPosition() {
            return this.i;
        }
    }
}
