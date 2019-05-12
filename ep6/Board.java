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

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.lang.IllegalArgumentException;

import static org.junit.Assert.*;

public class Board {
    private int[][] board;
    private int n;
    private int hammingDist;
    private int manhattanDist;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.n = tiles[0].length;
        this.board = new int[n][n];
        int correctNumber = 1;
        int hamming = 0;
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.board[i][j] = tiles[i][j];

                int currentNumber = tiles[i][j];
                if (currentNumber != correctNumber && currentNumber != 0) {
                    hamming++;

                    int[] currentCoord = new int[]{i, j};
                    int[] goalCoord = findGoalBoardCoordOf(currentNumber);
                    manhattan += Math.abs(currentCoord[0] - goalCoord[0]) + Math.abs(currentCoord[1] - goalCoord[1]);
                }
                correctNumber++;
            }
        }

        this.hammingDist = hamming;
        this.manhattanDist = manhattan;
    }

    // string representation of this board
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        boardString.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boardString.append(board[i][j]);
                boardString.append(" ");
            }
            boardString.append("\n");
        }

        return boardString.toString();
    }

    // tile at (row, col) or 0 if blank
    public int tileAt(int row, int col) {
        if (row < 0 || row > n - 1 || col < 0 || col > n - 1) {
            throw new IllegalArgumentException();
        }

        return this.board[row][col];
    }

    // board size n
    public int size() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDist;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;

        if (this.size() != that.size()) return false;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tileAt(i, j) != that.tileAt(i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighborStack = new Stack<Board>();

        int[] blankCoords = findBlankCoordinates();
        int blankRow = blankCoords[0];
        int blankCol = blankCoords[1];

        //cima
        if (blankRow != 0) {
            int temp = this.board[blankRow - 1][blankCol];
            this.board[blankRow - 1][blankCol] = 0;
            this.board[blankRow][blankCol] = temp;

            neighborStack.push(new Board(this.board));

            this.board[blankRow - 1][blankCol] = temp;
            this.board[blankRow][blankCol] = 0;
        }

        //baixo
        if (blankRow != n - 1) {
            int temp = this.board[blankRow + 1][blankCol];
            this.board[blankRow + 1][blankCol] = 0;
            this.board[blankRow][blankCol] = temp;

            neighborStack.push(new Board(this.board));

            this.board[blankRow + 1][blankCol] = temp;
            this.board[blankRow][blankCol] = 0;
        }

        //esquerda
        if (blankCol != 0) {
            int temp = this.board[blankRow][blankCol - 1];
            this.board[blankRow][blankCol - 1] = 0;
            this.board[blankRow][blankCol] = temp;

            neighborStack.push(new Board(this.board));

            this.board[blankRow][blankCol - 1] = temp;
            this.board[blankRow][blankCol] = 0;
        }

        //direita
        if (blankCol != n - 1) {
            int temp = this.board[blankRow][blankCol + 1];
            this.board[blankRow][blankCol + 1] = 0;
            this.board[blankRow][blankCol] = temp;

            neighborStack.push(new Board(this.board));

            this.board[blankRow][blankCol + 1] = temp;
            this.board[blankRow][blankCol] = 0;
        }

        return neighborStack;
    }

    // is this board solvable?
    public boolean isSolvable() {
        int inversions = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int baseNumber = this.tileAt(i, j);
                boolean adjustedFirstCol = false;

                for (int k = i; k < n; k++) {
                    for (int l = 0; l < n; l++) {
                        if (!adjustedFirstCol) {
                            l = j;
                            adjustedFirstCol = true;
                        }
                        int currentNumber = this.tileAt(k,l);
                        if (currentNumber !=0 && currentNumber < baseNumber) {
                            inversions++;
                        }
                    }
                }
            }
        }

        boolean solvable = true;
        if (n % 2 == 1) {
            if (inversions % 2 != 0) {
                solvable = false;
            }
        } else {
            if ((inversions + findBlankCoordinates()[0]) % 2 != 1) {
                solvable = false;
            }
        }

        return solvable;
    }

    /**
     * Private Methods
     */

    private int[] findGoalBoardCoordOf(int number) {
        int row = number / n;
        int col = number % n - 1;

        if (col == -1) {
            col = n - 1;
            row--;
        }

        return new int[]{row, col};
    }

    private int[] findBlankCoordinates() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.tileAt(i, j) == 0) {
                    return new int[]{i, j};
                }
            }
        }
        throw new RuntimeException("Board has no blank tile");
    }

    /**
     * Tests
     */

    private static void testToString(int[][] tiles) {
        Board b = new Board(tiles);

        StdOut.println("\nTesting toString:\n" + b.toString());
    }

    private static void testTileAt(int[][] tiles) {
        Board b = new Board(tiles);

        assertEquals(b.tileAt(0, 0), 1);
        assertEquals(b.tileAt(0, 1), 0);
        assertEquals(b.tileAt(1, 1), 2);
        assertEquals(b.tileAt(2, 2), 6);
        assertEquals(b.tileAt(2, 0), 7);
    }

    private static void testHammingDistance(int[][] tiles) {
        Board b = new Board(tiles);
        assertEquals(b.hamming(), 5);
    }

    private static void testManhattanDistance(int[][] tiles) {
        Board b = new Board(tiles);
        assertEquals(b.manhattan(), 10);
    }

    private static void testFindGoalBoardCoordOf(int[][] tiles) {
        Board b = new Board(tiles);
        assertEquals(b.findGoalBoardCoordOf(8)[0], 2);
        assertEquals(b.findGoalBoardCoordOf(8)[1], 1);

        assertEquals(b.findGoalBoardCoordOf(7)[0], 2);
        assertEquals(b.findGoalBoardCoordOf(7)[1], 0);

        assertEquals(b.findGoalBoardCoordOf(6)[0], 1);
        assertEquals(b.findGoalBoardCoordOf(6)[1], 2);

        assertEquals(b.findGoalBoardCoordOf(4)[0], 1);
        assertEquals(b.findGoalBoardCoordOf(4)[1], 0);

        assertEquals(b.findGoalBoardCoordOf(3)[0], 0);
        assertEquals(b.findGoalBoardCoordOf(3)[1], 2);

        assertEquals(b.findGoalBoardCoordOf(2)[0], 0);
        assertEquals(b.findGoalBoardCoordOf(2)[1], 1);
    }

    private static void testGoalBoardDistances(int[][] tiles) {
        Board b = new Board(tiles);
        assertEquals(b.hamming(), 0);
        assertEquals(b.manhattan(), 0);
    }

    private static void testIsGoalWithGoalBoard(int[][] tiles) {
        Board b = new Board(tiles);
        assertTrue(b.isGoal());
    }

    private static void testIsGoalWithNonGoalBoard(int[][] tiles) {
        Board b = new Board(tiles);
        assertFalse(b.isGoal());
    }

    private static void testEquals(int[][] tiles1, int[][] tiles2, int[][] differentTiles) {
        Board b1 = new Board(tiles1);
        Board b1Copy = new Board(tiles1);
        Board b2 = new Board(tiles2);
        Board differentBoard = new Board(differentTiles);

        assertEquals(b1, b1Copy);
        assertEquals(b1, b2);
        assertTrue(b1.equals(b2));
        assertFalse(b1.equals(null));
        assertFalse(b1.equals(differentBoard));
        assertFalse(b2.equals(differentBoard));
        assertFalse(b1Copy.equals(differentBoard));
        assertTrue(differentBoard.equals(differentBoard));
    }

    private static void testNeighbors(int[][] tiles) {
        Board b = new Board(tiles);

        Stack<Board> neighborStack = (Stack<Board>)b.neighbors();

        StdOut.println("Testing the three neighbours of the previous matrix:");
        StdOut.println(neighborStack.pop().toString());
        StdOut.println(neighborStack.pop().toString());
        StdOut.println(neighborStack.pop().toString());
    }

    private static void testIsSolvableWithSolvableBoard(int[][] tiles) {
        Board b = new Board(tiles);

        assertTrue(b.isSolvable());
    }

    private static void testIsSolvableWithUnsolvableBoard(int[][] tiles) {
        Board b = new Board(tiles);

        assertFalse(b.isSolvable());
    }


    // unit testing (required)
    public static void main(String[] args) {
        int[][] threeByThree = new int[3][3];
        int[] threeByThreeInputs = {1, 0, 3, 4, 2, 5, 7, 8, 6};

        int[][] distanceTestMatrix = new int[3][3];
        int[] distanceTestInputs = {8, 1, 3, 4, 0, 2, 7, 6, 5};

        int[][] threeGoalBoard = new int[3][3];
        int[][] threeGoalBoardCopy = new int[3][3];
        int[] threeGoalBoardInputs = {1, 2, 3, 4, 5, 6, 7, 8, 0};

        int[][] unsolvableBoard3by3 = new int[3][3];
        int[] unsolvableBoard3by3Inputs = {1, 2, 3, 4, 5, 6, 8, 7, 0};

        int[][] unsolvableBoard4by4 = new int[4][4];
        int[] unsolvableBoard4by4Inputs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 14, 0};

        int arrayIndex = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                threeByThree[i][j] = threeByThreeInputs[arrayIndex];
                distanceTestMatrix[i][j] = distanceTestInputs[arrayIndex];
                threeGoalBoard[i][j] = threeGoalBoardInputs[arrayIndex];
                threeGoalBoardCopy[i][j] = threeGoalBoardInputs[arrayIndex];
                unsolvableBoard3by3[i][j] = unsolvableBoard3by3Inputs[arrayIndex];
                unsolvableBoard4by4[i][j] = unsolvableBoard4by4Inputs[arrayIndex];

                arrayIndex++;
            }
        }

        testToString(threeByThree);
        testTileAt(threeByThree);

        testHammingDistance(distanceTestMatrix);
        testManhattanDistance(distanceTestMatrix);

        testFindGoalBoardCoordOf(distanceTestMatrix);
        testGoalBoardDistances(threeGoalBoard);
        testIsGoalWithGoalBoard(threeGoalBoard);
        testIsGoalWithNonGoalBoard(threeByThree);
        testIsGoalWithNonGoalBoard(distanceTestMatrix);

        testEquals(threeGoalBoard, threeGoalBoardCopy, threeByThree);
        testNeighbors(threeByThree);

        testIsSolvableWithSolvableBoard(threeByThree);
        testIsSolvableWithSolvableBoard(distanceTestMatrix);
        testIsSolvableWithSolvableBoard(threeGoalBoard);

        testIsSolvableWithUnsolvableBoard(unsolvableBoard3by3);
        testIsSolvableWithUnsolvableBoard(unsolvableBoard4by4);

        StdOut.println("\nAll tests ran with no errors.\n");
    }
}