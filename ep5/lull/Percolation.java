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
 -Ideia de juntar os sítios do topo em um sítio virtual para otimizar a verificação de isFull:
 https://github.com/akosiorek/algorithms/blob/master/coursera/algorithms/src/Percolation.java

 Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
 ****************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import static org.junit.Assert.*;

public class Percolation {
    private boolean[][] isOpenGrid;
    private WeightedQuickUnionUF unionFind;
    private int openSites = 0;
    private int n;
    private int sourceSite;
    private int bottomSite;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        isOpenGrid = new boolean[n][n];
        unionFind = new WeightedQuickUnionUF(n * n + 2);
        this.sourceSite = n * n;
        this.bottomSite = n * n + 1;
        this.n = n;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (argumentsAreOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }

        if (!isOpenGrid[row][col]) {
            isOpenGrid[row][col] = true;
            openSites++;

            verifyNeighboursAndUnite(row, col);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (argumentsAreOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }
        return isOpenGrid[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (argumentsAreOutOfBounds(row, col)) {
            throw new IllegalArgumentException();
        }

        int p = matrixToUnionCoordinatesConverter(row, col);
        return unionFind.connected(p, sourceSite);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return unionFind.connected(sourceSite, bottomSite);
    }


    /***************
     Private Methods
     ****************/

    // throw an IllegalArgumentException if row and col are out of bounds
    private boolean argumentsAreOutOfBounds(int row, int col) {
        if (row < 0 || row > n - 1 || col < 0 || col > n - 1) {
            return true;
        }

        return false;
    }

    // transforms the 2D matrix coordinates to match the 1D union find
    private int matrixToUnionCoordinatesConverter(int row, int col) {
        return row * n + col;
    }

    // verify if the upper neighbour is open, if possible
    private boolean verifyUpNeighbourOpenness(int row, int col) {
        if (row == 0) {
            return false;
        }
        return isOpen(row - 1, col);
    }

    // verify if the left neighbour is open, if possible
    private boolean verifyLeftNeighbourOpenness(int row, int col) {
        if (col == 0) {
            return false;
        }
        return isOpen(row, col - 1);
    }

    // verify if the right neighbour is open, if possible
    private boolean verifyRightNeighbourOpenness(int row, int col) {
        if (col == n - 1) {
            return false;
        }
        return isOpen(row, col + 1);
    }

    // verify if the right neighbour is open, if possible
    private boolean verifyDownNeighbourOpenness(int row, int col) {
        if (row == n - 1) {
            return false;
        }
        return isOpen(row + 1, col);
    }

    // check if any neighbour is full
    private boolean verifyNeighboursFullness(int row, int col) {
        if (row != 0) {
            if (isFull(row - 1, col)) {
                return true;
            }
        }
        if (row != n - 1) {
            if (isFull(row + 1, col)) {
                return true;
            }
        }
        if (col != 0) {
            if (isFull(row, col - 1)) {
                return true;
            }
        }
        if (col != n - 1) {
            return isFull(row, col + 1);
        }

        return false;
    }

    // verify each neighbour and unite if they are open
    // it also unites the top sites with the virtual sourceSite and the bottom sites with the virtual bottomSite
    private void verifyNeighboursAndUnite(int row, int col) {
        int p = matrixToUnionCoordinatesConverter(row, col);
        int q;

        if (row == 0) {
            unionFind.union(p, sourceSite);
        } else if (row == n - 1) {
            unionFind.union(p, bottomSite);
        }

        if (verifyUpNeighbourOpenness(row, col)) {
            q = matrixToUnionCoordinatesConverter(row - 1, col);
            unionFind.union(p, q);
        }
        if (verifyLeftNeighbourOpenness(row, col)) {
            q = matrixToUnionCoordinatesConverter(row, col - 1);
            unionFind.union(p, q);
        }
        if (verifyRightNeighbourOpenness(row, col)) {
            q = matrixToUnionCoordinatesConverter(row, col + 1);
            unionFind.union(p, q);
        }
        if (verifyDownNeighbourOpenness(row, col)) {
            q = matrixToUnionCoordinatesConverter(row + 1, col);
            unionFind.union(p, q);
        }
    }


    /*****
     Tests
     *****/

    private static void testMatrixToUnionCoordinatesConverter() {
        Percolation perc = new Percolation(20);

        assertEquals(perc.matrixToUnionCoordinatesConverter(0, 15), 15);
        assertEquals(perc.matrixToUnionCoordinatesConverter(0, 0), 0);
        assertEquals(perc.matrixToUnionCoordinatesConverter(1, 2), 22);
        assertEquals(perc.matrixToUnionCoordinatesConverter(1, 0), 20);
        assertEquals(perc.matrixToUnionCoordinatesConverter(2, 3), 43);
        assertEquals(perc.matrixToUnionCoordinatesConverter(10, 10), 210);
    }

    private static void testVerifyUpNeighbourOpenness() {
        Percolation perc = new Percolation(20);

        assertFalse(perc.verifyUpNeighbourOpenness(0, 5));
        assertFalse(perc.verifyUpNeighbourOpenness(0, 10));
        assertFalse(perc.verifyUpNeighbourOpenness(5, 10));

        perc.open(6, 10);

        assertFalse(perc.verifyUpNeighbourOpenness(5, 10));

        perc.open(4, 10);

        assertTrue(perc.verifyUpNeighbourOpenness(5, 10));
    }

    private static void testVerifyDownNeighbourOpenness() {
        Percolation perc = new Percolation(20);

        assertFalse(perc.verifyDownNeighbourOpenness(19, 5));
        assertFalse(perc.verifyDownNeighbourOpenness(19, 10));
        assertFalse(perc.verifyDownNeighbourOpenness(5, 10));

        perc.open(4, 10);

        assertFalse(perc.verifyDownNeighbourOpenness(5, 10));

        perc.open(6, 10);

        assertTrue(perc.verifyDownNeighbourOpenness(5, 10));
    }

    private static void testVerifyLeftNeighbourOpenness() {
        Percolation perc = new Percolation(20);

        assertFalse(perc.verifyLeftNeighbourOpenness(5, 0));
        assertFalse(perc.verifyLeftNeighbourOpenness(10, 0));
        assertFalse(perc.verifyLeftNeighbourOpenness(10, 5));

        perc.open(10, 6);

        assertFalse(perc.verifyLeftNeighbourOpenness(10, 5));

        perc.open(10, 4);

        assertTrue(perc.verifyLeftNeighbourOpenness(10, 5));
    }

    private static void testVerifyRightNeighbourOpenness() {
        Percolation perc = new Percolation(20);

        assertFalse(perc.verifyRightNeighbourOpenness(5, 19));
        assertFalse(perc.verifyRightNeighbourOpenness(10, 19));
        assertFalse(perc.verifyRightNeighbourOpenness(10, 5));

        perc.open(10, 4);

        assertFalse(perc.verifyRightNeighbourOpenness(10, 5));

        perc.open(10, 6);

        assertTrue(perc.verifyRightNeighbourOpenness(10, 5));
    }

    private static void testVerifyNeighboursFullness() {
        Percolation perc = new Percolation(20);

        assertFalse(perc.verifyNeighboursFullness(0, 0));
        assertFalse(perc.verifyNeighboursFullness(10, 10));
        assertFalse(perc.verifyNeighboursFullness(0, 10));
        assertFalse(perc.verifyNeighboursFullness(10, 0));
        assertFalse(perc.verifyNeighboursFullness(19, 10));
        assertFalse(perc.verifyNeighboursFullness(10, 10));
        assertFalse(perc.verifyNeighboursFullness(19, 19));

        perc.open(1, 5);

        assertFalse(perc.verifyNeighboursFullness(1, 5));

        perc.open(0, 5);

        assertTrue(perc.verifyNeighboursFullness(1, 5));
        assertTrue(perc.verifyNeighboursFullness(2, 5));
        assertTrue(perc.verifyNeighboursFullness(1, 6));
        assertTrue(perc.verifyNeighboursFullness(1, 4));
        assertTrue(perc.verifyNeighboursFullness(0, 5));
    }

    private static void testVerifyNeighboursAndUnite() {
        Percolation perc = new Percolation(20);
        int p = perc.matrixToUnionCoordinatesConverter(5, 5);
        int q = perc.matrixToUnionCoordinatesConverter(5, 6);

        assertFalse(perc.unionFind.connected(p, q));

        perc.open(5, 5);

        assertFalse(perc.unionFind.connected(p, q));

        perc.open(5, 7);

        assertFalse(perc.unionFind.connected(p, q));

        perc.open(5, 6);

        assertTrue(perc.unionFind.connected(p, q));

        perc.open(0, 0);
        p = perc.matrixToUnionCoordinatesConverter(0, 0);
        assertTrue(perc.unionFind.connected(perc.sourceSite, p));

        perc.open(19, 0);
        p = perc.matrixToUnionCoordinatesConverter(19, 0);
        assertTrue(perc.unionFind.connected(perc.bottomSite, p));
    }

    public static void testNumberOfOpenSites() {
        Percolation perc = new Percolation(20);

        assertEquals(perc.numberOfOpenSites(), 0);

        perc.open(5, 5);

        assertEquals(perc.numberOfOpenSites(), 1);

        perc.open(5, 5);

        assertEquals(perc.numberOfOpenSites(), 1);

        perc.open(5, 6);
        perc.open(5, 7);
        perc.open(5, 8);

        assertEquals(perc.numberOfOpenSites(), 4);
    }

    public static void testPercolates() {
        Percolation perc = new Percolation(5);

        assertFalse(perc.percolates());

        perc.open(0, 0);
        perc.open(1, 0);
        perc.open(2, 0);
        perc.open(3, 0);

        assertFalse(perc.percolates());

        perc.open(4, 1);

        assertFalse(perc.percolates());

        perc.open(4, 0);

        assertTrue(perc.percolates());
    }

    private static void testArgumentsOutOfBounds() {
        Percolation perc = new Percolation(5);
        boolean thrown = false;

        try {
            perc.open(-1, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(1, -1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(-1, -1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(10, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(1, 10);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(5, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.open(10, -5);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.isOpen(-1, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.isOpen(10, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.isFull(-1, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
        thrown = false;

        try {
            perc.isFull(10, 1);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    private static void testConstructorWithNegativeN(){
        boolean thrown = false;

        try {
            Percolation perc = new Percolation(-5);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        assertTrue(thrown);
    }

    // unit testing (required)
    public static void main(String[] args) {
        // Almost all of the tests below call the Percolation constructor and the open() method, which
        // calls isOpen() and isFull(), so I didn't create separate tests for those 4 functions
        testMatrixToUnionCoordinatesConverter();
        testVerifyUpNeighbourOpenness();
        testVerifyDownNeighbourOpenness();
        testVerifyLeftNeighbourOpenness();
        testVerifyRightNeighbourOpenness();
        testVerifyNeighboursFullness();
        testVerifyNeighboursAndUnite();
        testNumberOfOpenSites();
        testPercolates();
        testArgumentsOutOfBounds();
        testConstructorWithNegativeN();

        StdOut.println("\nAll tests ran with no errors.\n");
    }
}
