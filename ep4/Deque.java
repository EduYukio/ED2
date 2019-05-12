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
 -Implementação do Deque (adaptado):
 https://codereview.stackexchange.com/questions/56361/generic-deque-implementation


 Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
 ****************************************************************/

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.IllegalArgumentException;

import static org.junit.Assert.*;

public class Deque<Item> implements Iterable<Item> {
    private Node<Item> head, tail;
    private int size;

    // construct an empty deque
    public Deque() {
    }

    // is the deque empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node<Item> prevHead = head;
        Node<Item> newHead = new Node<Item>(item);
        if (prevHead != null) {
            prevHead.connectRight(newHead);
        } else {
            tail = newHead;
        }
        head = newHead;
        size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node<Item> newTail = new Node<Item>(item);
        Node<Item> prevTail = tail;
        if (prevTail != null) {
            newTail.connectRight(prevTail);
        } else {
            head = newTail;
        }
        tail = newTail;
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        size--;
        Item headItem = head.item;
        Node<Item> headLeftNode = head.left;
        if (headLeftNode != null) {
            headLeftNode.right = null;
        }
        head = headLeftNode;
        return headItem;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        size--;
        Item tailItem = tail.item;
        Node<Item> tailRightNode = tail.right;

        if (tailRightNode != null) {
            tailRightNode.left = null;
        }
        tail = tailRightNode;
        return tailItem;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }



    /* *********
     Sub Classes
    ************ */

    private class Node<Item> {
        public Node<Item> left, right;
        private final Item item;

        private Node(Item item) {
            this.item = item;
        }

        private void connectRight(Node<Item> rightNode) {
            this.right = rightNode;
            rightNode.left = this;
        }
    }

    private class DequeIterator implements Iterator<Item> {
        private Node<Item> current = head;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.left;
            return item;
        }
    }


    /* *********
        Tests
    ************ */
    private static void testEmptinessBeforeAndAfterAddition() {
        Deque<Double> d = new Deque<Double>();
        assertTrue(d.isEmpty());
        d.addFirst(2.0);
        assertFalse(d.isEmpty());
        assertEquals(1, d.size());
    }

    private static void testAddFirstAndRemoveTwoValues() {
        Deque<Double> d = new Deque<Double>();
        d.addFirst(1.0);
        d.addFirst(2.0);

        assertEquals(Double.valueOf(2.0), d.removeFirst());
        assertEquals(Double.valueOf(1.0), d.removeFirst());
    }

    private static void testAddLastAndRemoveTwoValues() {
        Deque<Double> d = new Deque<Double>();
        d.addLast(1.0);
        d.addLast(2.0);

        assertEquals(Double.valueOf(2.0), d.removeLast());
        assertEquals(Double.valueOf(1.0), d.removeLast());
    }

    private static void testAddFirstAndLastAndRemove() {
        Deque<Double> d = new Deque<Double>();
        d.addFirst(1.0);
        d.addLast(2.0);
        assertEquals(Double.valueOf(1.0), d.removeFirst());
        assertEquals(Double.valueOf(2.0), d.removeLast());

        d.addLast(3.0);
        d.addFirst(4.0);
        assertEquals(Double.valueOf(3.0), d.removeLast());
        assertEquals(Double.valueOf(4.0), d.removeFirst());
    }

    private static void testAddLastAndFirstAndRemove() {
        Deque<Double> d = new Deque<Double>();

        d.addLast(3.0);
        d.addFirst(4.0);
        assertEquals(Double.valueOf(3.0), d.removeLast());
        assertEquals(Double.valueOf(4.0), d.removeFirst());
    }

    private static void testIterator() {
        Deque<Double> d = new Deque<Double>();
        for (Double i = 0d; i < 10; i++) {
            d.addLast(i);
        }
        int i = 0;
        for (Double currentValue : d) {
            assertEquals(Double.valueOf(i), currentValue);
            i++;
        }
        assertEquals(Double.valueOf(i), Double.valueOf(10));
    }

    private static void testRemoveFirstOnEmpty() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            d.removeFirst();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testRemoveLastOnEmpty() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            d.removeLast();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testIteratorNextOnEmpty() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            Iterator<Double> itr = d.iterator();
            assertFalse(itr.hasNext());
            itr.next();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testIteratorRemoveMethod() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            Iterator<Double> itr = d.iterator();
            itr.remove();
        } catch (UnsupportedOperationException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testAddFirstWithNull() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            d.addFirst(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testAddLastWithNull() {
        boolean thrown = false;

        try {
            Deque<Double> d = new Deque<Double>();
            d.addLast(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    // unit testing (required)
    public static void main(String[] args) {
        testEmptinessBeforeAndAfterAddition();
        testAddFirstAndRemoveTwoValues();
        testAddLastAndRemoveTwoValues();
        testAddFirstAndLastAndRemove();
        testAddLastAndFirstAndRemove();
        testIterator();
        testRemoveFirstOnEmpty();
        testRemoveLastOnEmpty();
        testIteratorNextOnEmpty();
        testIteratorRemoveMethod();
        testAddFirstWithNull();
        testAddLastWithNull();

        StdOut.println("\nAll tests ran with no errors.");
    }
}