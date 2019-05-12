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
 -Randomized Queue implementation (adaptado)
 https://stackoverflow.com/questions/31143654/data-structures-randomized-queues

 -Fisher-Yates shuffle algorithm
 https://www.youtube.com/watch?v=tLxBwSL3lPQ


 Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
 ****************************************************************/

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.*;

public class RandomizedQueue<Item> implements Iterable<Item> {
    @SuppressWarnings("unchecked")
    private Item[] queue = (Item[]) new Object[1];
    private int queueEndIndex = 0;
    private Random randomNumberGenerator = new Random();

    // changes the queue size to the specified size.
    private void resizeQueue(int newSize) {
        queue = Arrays.copyOfRange(queue, 0, newSize);
    }

    // construct an empty randomized queue
    public RandomizedQueue() {
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return queueEndIndex == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return queueEndIndex;
    }

    // add the item
    public void enqueue(Item elem) {
        if (elem == null) {
            throw new IllegalArgumentException();
        }

        if (queueEndIndex == queue.length) {
            resizeQueue(queue.length * 2);
        }

        queue[queueEndIndex] = elem;
        queueEndIndex++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        int randomIndex = randomNumberGenerator.nextInt(queueEndIndex);
        Item randomIndexValue = queue[randomIndex];
        queueEndIndex--;
        queue[randomIndex] = queue[queueEndIndex];
        queue[queueEndIndex] = null;

        if (size() <= queue.length / 4) {
            resizeQueue(queue.length / 2);
        }

        return randomIndexValue;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int index = randomNumberGenerator.nextInt(queueEndIndex);
        return queue[index];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }



    /* *********
      Sub Classes
    ************ */

    // every iteration will return entries in a different order.
    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] shuffledArray;
        private int current = 0;

        private RandomizedQueueIterator() {
            shuffledArray = queue.clone();
            shuffle(shuffledArray);
        }

        public boolean hasNext() {
            return current < queueEndIndex;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return shuffledArray[current++];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        // using the Fisher-Yates Shuffle algorithm, it rearranges the array in an uniformly random order
        private void shuffle(Item[] array) {
            for (int i = queueEndIndex - 1; i >= 0; i--) {
                int randomIndex = randomNumberGenerator.nextInt(i + 1);
                Item temp = array[randomIndex];
                array[randomIndex] = array[i];
                array[i] = temp;
            }
        }
    }


    /* *********
        Tests
    ************ */
    private static void testEmptinessBeforeAndAfterAddition() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        assertTrue(q.isEmpty());
        q.enqueue(1);
        assertFalse(q.isEmpty());
    }

    private static void testSizeBeforeAndAfterAdding() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        assertEquals(Integer.valueOf(q.size()), Integer.valueOf(0));
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        assertEquals(Integer.valueOf(q.size()), Integer.valueOf(3));
    }

    private static void testEnqueueDequeueUnitaryArray() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        q.enqueue(42);
        assertEquals(q.dequeue(), Integer.valueOf(42));
        assertTrue(q.isEmpty());
    }

    private static void testEnqueueDequeueTenValues() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }

        StdOut.println("\n\nTesting Dequeue:\nThe dequeue returns a random number from the array,\nso they are " +
                "expected to be picked randomly here, but without repetitions.");

        StdOut.println("Original array: ");
        for (int i = 0; i < 10; i++) {
            StdOut.print(i + " ");
        }
        StdOut.println();

        StdOut.println("Calling dequeue() 10 times: ");
        for (int i = 0; i < 10; i++) {
            StdOut.print(q.dequeue() + " ");
        }
        StdOut.println();

        assertTrue(q.isEmpty());
    }

    private static void testSample() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }

        StdOut.println("\n\nTesting Sample:\nThe sample returns a random number from the array,\nso they are expected" +
                " to be picked randomly here, and it can contain repetitions.");

        StdOut.println("Original array: ");
        for (int i = 0; i < 10; i++) {
            StdOut.print(i + " ");
        }
        StdOut.println();

        StdOut.println("Calling sample() 10 times: ");
        for (int i = 0; i < 10; i++) {
            StdOut.print(q.sample() + " ");
        }
        StdOut.println();
    }

    private static void testIterator() {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();
        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }

        StdOut.println("\n\nTesting Iterator:\nThe iterator returns a random number from the array,\nso they are " +
                "expected to be picked randomly here, but without repetitions.");

        StdOut.println("Original array: ");
        for (int i = 0; i < 10; i++) {
            StdOut.print(i + " ");
        }
        StdOut.println();


        StdOut.println("Iterating through the array:");
        for (Integer elem : q) {
            System.out.print(elem + " ");
        }
        StdOut.println();
    }

    private static void testEnqueueNull() {
        boolean thrown = false;

        try {
            RandomizedQueue<Integer> q = new RandomizedQueue<>();
            q.enqueue(null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testSampleOnEmptyArray() {
        boolean thrown = false;

        try {
            RandomizedQueue<Integer> q = new RandomizedQueue<>();
            q.sample();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testDequeueOnEmptyArray() {
        boolean thrown = false;

        try {
            RandomizedQueue<Integer> q = new RandomizedQueue<>();
            q.dequeue();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testIteratorNextOnEmpty() {
        boolean thrown = false;

        try {
            RandomizedQueue<Integer> q = new RandomizedQueue<>();
            Iterator<Integer> itr = q.iterator();
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
            RandomizedQueue<Integer> q = new RandomizedQueue<>();
            Iterator<Integer> itr = q.iterator();
            itr.remove();
        } catch (UnsupportedOperationException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    // unit testing (required)
    public static void main(String[] args) {
        testEmptinessBeforeAndAfterAddition();
        testSizeBeforeAndAfterAdding();
        testEnqueueDequeueUnitaryArray();
        testEnqueueDequeueTenValues();
        testSample();
        testIterator();
        testEnqueueNull();
        testSampleOnEmptyArray();
        testDequeueOnEmptyArray();
        testIteratorNextOnEmpty();
        testIteratorRemoveMethod();

        StdOut.println("\nAll tests ran with no errors.");
    }
}