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

public class PointST<Value> {
    private RedBlackBST<Point2D, Value> symbolTable;

    // construct an empty symbol table of points
    public PointST() {
        this.symbolTable = new RedBlackBST<>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return symbolTable.isEmpty();
    }

    // number of points
    public int size() {
        return symbolTable.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new IllegalArgumentException();
        }

        symbolTable.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        return symbolTable.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        return symbolTable.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return symbolTable.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }

        Stack<Point2D> insidePoints = new Stack<>();

        Queue<Point2D> points = (Queue<Point2D>) points();
        for (Point2D i : points) {
            if (i.x() >= rect.xmin() && i.x() <= rect.xmax() &&
                    i.y() >= rect.ymin() && i.y() <= rect.ymax()) {
                insidePoints.push(i);
            }
        }
        return insidePoints;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }

        if (symbolTable.isEmpty()) return null;
        double minDist = Double.MAX_VALUE;
        Point2D champion = null;

        Queue<Point2D> points = (Queue<Point2D>) points();
        for (Point2D i : points) {
            double currentDist = i.distanceSquaredTo(p);
            if (currentDist < minDist) {
                minDist = currentDist;
                champion = i;
            }
        }

        return champion;
    }

    // unit testing (required)
    public static void main(String[] args) {
        PointST<Integer> st = new PointST<>();
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

        StdOut.println(st.range(new RectHV(0, 4, 1, 6)));
        StdOut.println(st.range(new RectHV(1.1, 2.2, 3.2, 3.2)));
        StdOut.println(st.range(new RectHV(4, 1, 5, 4.9)));

        StdOut.println(st.nearest(new Point2D(4.2, 1.3)));
        StdOut.println(st.nearest(new Point2D(1.1, 4.6)));
        StdOut.println(st.nearest(new Point2D(2.3, 3.5)));
        StdOut.println(st.nearest(new Point2D(4, 3)));

        assertEquals(st.get(new Point2D(2, 3)), Integer.valueOf(5));
        assertEquals(st.get(new Point2D(4, 4)), Integer.valueOf(10));

        assertTrue(st.contains(new Point2D(4, 4)));
        assertTrue(st.contains(new Point2D(1, 5)));
        assertTrue(st.contains(new Point2D(4, 5)));
        assertFalse(st.contains(new Point2D(9, 9)));
        assertFalse(st.contains(new Point2D(25, 5)));

        assertFalse(st.isEmpty());
        assertEquals(st.size(), 6);
    }
}