/****************************************************************
 Nome: Eduardo Yukio Rodrigues
 NUSP: 8988702

 Ao preencher esse cabeçalho com o meu nome e o meu número USP,
 declaro que todas as partes originais desse exercício programa (EP)
 foram desenvolvidas e implementadas por mim e que portanto não
 constituem desonestidade acadêmica ou plágio.
 Declaro também que sou responsável por todas as cópias desse
 programa e que não distribui ou facilitei a sua distribuição.
 Estou ciente que os casos de plágio e desonestidade acadêmica
 serão tratados segundo os critérios divulgados na página da
 disciplina.
 Entendo que EPs sem assinatura devem receber nota zero e, ainda
 assim, poderão ser punidos por desonestidade acadêmica.

 Abaixo descreva qualquer ajuda que você recebeu para fazer este
 EP.  Inclua qualquer ajuda recebida por pessoas (inclusive
 monitoras e colegas). Com exceção de material de MAC0323, caso
 você tenha utilizado alguma informação, trecho de código,...
 indique esse fato abaixo para que o seu programa não seja
 considerado plágio ou irregular.

 Exemplo:

 A monitora me explicou que eu devia utilizar a função xyz().

 O meu método xyz() foi baseada na descrição encontrada na
 página https://www.ime.usp.br/~pf/algoritmos/aulas/enumeracao.html.

 Descrição de ajuda ou indicação de fonte:

 Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
 ****************************************************************/

import edu.princeton.cs.algs4.*;

import java.lang.IllegalArgumentException;

import static org.junit.Assert.*;

public class KdTreeST<Value> {
    private Node root = null;
    private int n = 0;
    private double negInf = Double.NEGATIVE_INFINITY;
    private double posInf = Double.POSITIVE_INFINITY;
    private double minDist;
    private Point2D champion;

    private class Node {
        private Point2D p;   // the point
        private Value value; // the symbol table maps the point to this value
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        public Node(Point2D p, Value value, RectHV newRect) {
            this.p = p;
            this.value = value;
            this.rect = newRect;
        }
    }

    // construct an empty symbol table of points
    public KdTreeST() {
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points
    public int size() {
        return n;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new IllegalArgumentException();
        }
        root = put(root, p, val, 0, null, null);
    }

    private Node put(Node node, Point2D p, Value val, int level, Node parentNode, String childPosition) {
        if (node == null) {
            RectHV newRect = null;

            if (level == 0) {
                // root node
                newRect = new RectHV(negInf, negInf, posInf, posInf);
            } else {
                RectHV parentRect = parentNode.rect;
                double newXMin = parentRect.xmin();
                double newYMin = parentRect.ymin();
                double newXMax = parentRect.xmax();
                double newYMax = parentRect.ymax();
                if (childPosition.equals("left")) {
                    //max
                    if ((level + 1) % 2 == 0) {
                        newXMax = parentNode.p.x();
                    } else {
                        newYMax = parentNode.p.y();
                    }
                } else if (childPosition.equals("right")) {
                    //min
                    if ((level + 1) % 2 == 0) {
                        newXMin = parentNode.p.x();
                    } else {
                        newYMin = parentNode.p.y();
                    }
                }

                newRect = new RectHV(newXMin, newYMin, newXMax, newYMax);
            }

            n++;
            return new Node(p, val, newRect);
        }
        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - node.p.x();
        } else {
            cmp = p.y() - node.p.y();
        }

        if (cmp < 0) {
            node.lb = put(node.lb, p, val, level + 1, node, "left");
        } else if (cmp >= 0) {
            node.rt = put(node.rt, p, val, level + 1, node, "right");
        }
        return node;
    }

    // value associated with point p
    public Value get(Point2D p) {
        if(p == null){
            throw new IllegalArgumentException();
        }

        return get(root, p, 0);
    }

    private Value get(Node node, Point2D p, int level) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (node == null) {
            return null;
        }

        if (p.x() == node.p.x() && p.y() == node.p.y()) {
            return node.value;
        }

        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - node.p.x();
        } else {
            cmp = p.y() - node.p.y();
        }

        if (cmp < 0) {
            return get(node.lb, p, level + 1);
        } else if (cmp >= 0) {
            return get(node.rt, p, level + 1);
        }

        throw new IllegalStateException();
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if(p == null){
            throw new IllegalArgumentException();
        }

        return get(p) != null;
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        Queue<Point2D> bfsQueue = new Queue<>();
        Queue<Node> frontier = new Queue<>();

        frontier.enqueue(root);
        while (!frontier.isEmpty()) {
            Node currentNode = frontier.dequeue();
            bfsQueue.enqueue(currentNode.p);

            if (currentNode.lb != null) {
                frontier.enqueue(currentNode.lb);
            }
            if (currentNode.rt != null) {
                frontier.enqueue(currentNode.rt);
            }
        }

        return bfsQueue;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if(rect == null){
            throw new IllegalArgumentException();
        }

        Queue<Point2D> q = new Queue<>(); // points inside the QueryRect
        range(root, rect, q);
        return q;
    }

    private void range(Node node, RectHV rect, Queue<Point2D> q) {
        if (node == null) {
            return;
        }
        if (node.rect.intersects(rect)) {
            if (rect.contains(node.p)) {
                q.enqueue(node.p);
            }

            range(node.lb, rect, q);
            range(node.rt, rect, q);
        }
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if(p == null){
            throw new IllegalArgumentException();
        }

        this.minDist = Double.MAX_VALUE;
        this.champion = null;

        nearest(root, p, 0);
        return this.champion;
    }

    private void nearest(Node node, Point2D p, int level) {
        if (node == null) {
            return;
        }

        if (node.rect.distanceSquaredTo(p) >= minDist) {
            return;
        }

        double currentDist = node.p.distanceSquaredTo(p);
        if (currentDist < minDist) {
            minDist = currentDist;
            champion = node.p;
        }

        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - node.p.x();
        } else {
            cmp = p.y() - node.p.y();
        }

        if (cmp < 0) {
            nearest(node.lb, p, level + 1);
            nearest(node.rt, p, level + 1);
        } else if (cmp >= 0) {
            nearest(node.rt, p, level + 1);
            nearest(node.lb, p, level + 1);
        }
    }

//    public Iterable<Point2D> nearest(Point2D p, int k){
//    }

    // unit testing (required)
    public static void main(String[] args) {
        KdTreeST<Integer> st = new KdTreeST<>();
        assertTrue(st.isEmpty());
        assertEquals(st.size(), 0);

        st.put(new Point2D(2, 3), 5);
        assertFalse(st.isEmpty());
        assertEquals(st.size(), 1);

        st.put(new Point2D(4, 2), 5);
        st.put(new Point2D(4, 5), 5);
        st.put(new Point2D(3, 3), 5);
        st.put(new Point2D(1, 5), 5);
        st.put(new Point2D(4, 4), 10);
        assertFalse(st.isEmpty());
        assertEquals(st.size(), 6);

        assertTrue(st.contains(new Point2D(2, 3)));
        assertTrue(st.contains(new Point2D(4, 2)));
        assertTrue(st.contains(new Point2D(4, 5)));
        assertTrue(st.contains(new Point2D(3, 3)));
        assertTrue(st.contains(new Point2D(1, 5)));
        assertTrue(st.contains(new Point2D(4, 4)));
        assertFalse(st.contains(new Point2D(9, 9)));
        assertFalse(st.contains(new Point2D(25, 5)));

        assertEquals(st.get(new Point2D(2, 3)), Integer.valueOf(5));
        assertEquals(st.get(new Point2D(4, 4)), Integer.valueOf(10));

        StdOut.println(st.points());

        StdOut.println(st.range(new RectHV(0, 4, 1, 6)));
        StdOut.println(st.range(new RectHV(1.1, 2.2, 3.2, 3.2)));
        StdOut.println(st.range(new RectHV(4, 1, 5, 4.9)));
        StdOut.println();
        StdOut.println();
        StdOut.println(st.nearest(new Point2D(4.2, 1.3)));
        StdOut.println(st.nearest(new Point2D(1.1, 4.6)));
        StdOut.println(st.nearest(new Point2D(2.3, 3.5)));
        StdOut.println(st.nearest(new Point2D(4, 3)));

        assertFalse(st.isEmpty());
        assertEquals(st.size(), 6);
    }
}