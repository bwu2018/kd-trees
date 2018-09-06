
/******************************************************************************
 *  Name:    Brandon Wu
 *  NetID:   bwu2018
 *  Precept: P05
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *  
 *  Compilation:  javac-algs4 PointSET.java
 *  Execution:    java-algs4 PointSET
 *  Dependencies: Point2D.java RectHV.java 
 * 
 *  Description: Represents a set of points in the unit square 
 *  (all points have x- and y-coordinates between 0 and 1) 
 *  using a red-black BST to support range search 
 *  (find all of the points contained in a query rectangle) 
 *  and nearest-neighbor search (find a closest point to a query point).
 ******************************************************************************/

import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        if (set.size() == 0) {
            return true;
        }
        return false;
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : set) { 
            point.draw();
        }
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> points = new Stack<Point2D>(); 
        for (Point2D point : set) {
            if (rect.contains(point)) {
                points.push(point);
            }
        }
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (set.isEmpty()) {
            return null;
        }
        Point2D min = set.first();
        for (Point2D point : set) {
            if (point.distanceSquaredTo(p) < min.distanceSquaredTo(p)) {
                min = point;
            }
        }
        return min;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
