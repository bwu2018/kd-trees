
/******************************************************************************
 *  Name:    Brandon Wu
 *  NetID:   bwu2018
 *  Precept: P05
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *  
 *  Compilation:  javac-algs4 KdTree.java
 *  Execution:    java-algs4 KdTree
 *  Dependencies: Point2D.java RectHV.java 
 * 
 *  Description: Represents a set of points in the unit square 
 *  (all points have x- and y-coordinates between 0 and 1) 
 *  using a 2d-tree to support efficient range search 
 *  (find all of the points contained in a query rectangle) 
 *  and nearest-neighbor search (find a closest point to a query point).
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private int size;
    private Node root;

    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        private boolean isVertical;

        Node(Point2D p, RectHV rect, Node lb, Node rt, boolean isVertical) {
            this.p = p;
            this.rect = rect;
            this.lb = lb;
            this.rt = rt;
            this.isVertical = isVertical;
        }
    }

    // construct an empty set of points
    public KdTree() {
        size = 0;
        root = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!this.contains(p)) {
            size++;
            root = insertHelper(root, p, true);
        }
    }

    private Node insertHelper(Node node, Point2D p, boolean isVertical) {
        double xmin = 0;
        double ymin = 0;
        double xmax = 1;
        double ymax = 1;
        if (node == null) {
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            Node newNode = new Node(p, rect, null, null, isVertical); // construct new node
            return newNode;
        }
        if (isVertical) {
            if (node.p.x() < p.x()) { // compare x values
                xmax = node.p.x();
                node.lb = insertHelper(node.lb, p, false);
            } else {
                xmin = node.p.x();
                node.rt = insertHelper(node.rt, p, false);
            }
        } else {
            if (node.p.y() < p.y()) { // compare y values
                ymax = node.p.y();
                node.lb = insertHelper(node.lb, p, true);
            } else {
                ymin = node.p.y();
                node.rt = insertHelper(node.rt, p, true);
            }
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return get(p) != null;
    }

    private Point2D get(Point2D p) {
        return get(root, p, true);
    }

    private Point2D get(Node node, Point2D p, boolean isVertical) {
        if (node == null) {
            return null;
        }
        if (node.p.x() == p.x() && node.p.y() == p.y()) {
            return node.p;
        }
        if (isVertical) {
            if (node.p.x() < p.x()) { // compare x values
                return get(node.lb, p, false);
            } else {
                return get(node.rt, p, false);
            }
        } else {
            if (node.p.y() < p.y()) { // compare y values
                return get(node.lb, p, true);
            } else {
                return get(node.rt, p, true);
            }
        }
    }

    /**
     * Returns the keys in the BST in level order (for debugging).
     *
     * @return the keys in the BST in level order traversal
     */
    private Iterable<Point2D> levelOrder() {
        Queue<Point2D> points = new Queue<Point2D>();
        Queue<Node> queue = new Queue<Node>();
        queue.enqueue(root);
        while (!queue.isEmpty()) {
            Node x = queue.dequeue();
            if (x == null)
                continue;
            points.enqueue(x.p);
            queue.enqueue(x.lb);
            queue.enqueue(x.rt);
        }
        return points;
    }

    // draw all points to standard draw
    public void draw() {
        // use inorder or preorder iterator
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : levelOrder()) {
            point.draw();
        }
    }

    private void rangeHelper(Node node, RectHV rect, Queue<Point2D> points) {
        if (node == null) {
            return;
        }
        if (!node.rect.intersects(rect)) {
            return;
        }
        if (rect.contains(node.p)) {
            points.enqueue(node.p);
        }
        rangeHelper(node.lb, rect, points);
        rangeHelper(node.rt, rect, points);
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        Queue<Point2D> points = new Queue<Point2D>();
        rangeHelper(root, rect, points);
        return points;
    }

    private Point2D nearestHelper(Point2D p, Node node, Point2D min) {
        if (node == null) {
            return min;
        }
        // closest point closer than distance from query and rectangle node
        if (min.distanceSquaredTo(p) < node.rect.distanceSquaredTo(p)) {
            return min;
        }
        if (min.distanceSquaredTo(p) > node.p.distanceSquaredTo(p)) {
            min = node.p;
        }
        if (node.isVertical && node.p.x() > p.x()) {
            min = nearestHelper(p, node.rt, min);
            min = nearestHelper(p, node.lb, min);
        } else {
            min = nearestHelper(p, node.lb, min);
            min = nearestHelper(p, node.rt, min);
        }
        return min;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            return null;
        }
        return nearestHelper(p, root, root.p);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        String filename = "kdtree-test-files/circle4.txt";
        In in = new In(filename);

        // initialize the two data structures with point from standard input
        // PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            System.out.println("Inserted " + p.toString());
            if (kdtree.contains(p)) {
                System.out.println(("Contains " + p.toString()));
            } else {
                System.out.println("Does not contain " + p.toString());
            }
            System.out.println("Size is " + kdtree.size());
            // brute.insert(p);
        }
    }
}
