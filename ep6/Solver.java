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
import java.util.Scanner;

import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    Node solutionNode;

    public class Node implements Comparable<Node> {
        Board board;
        int movesToReachBoard;
        Node previousNode;

        public Node(Board board, int moves, Node previousNode) {
            this.board = board;
            this.movesToReachBoard = moves;
            this.previousNode = previousNode;
        }

        public int compareTo(Node that) {
            return Integer.compare(manhattanPriorityFunc(this), manhattanPriorityFunc(that));
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Argument can't be null");
        }
        if (!initial.isSolvable()) {
            throw new IllegalArgumentException("Board is not solvable");
        }

        MinPQ<Node> priorityQueue = new MinPQ<>();
        Node initialNode = new Node(initial, 0, null);
        priorityQueue.insert(initialNode);

        while (true) {
            Node currentNode = priorityQueue.delMin();
            if (currentNode.board.isGoal()) {
                this.solutionNode = currentNode;
                return;
            }

            Stack<Board> neighborStack = (Stack<Board>) currentNode.board.neighbors();

            while (!neighborStack.isEmpty()) {
                Node neighbourNode = new Node(neighborStack.pop(), currentNode.movesToReachBoard + 1, currentNode);
                if (currentNode.previousNode == null) {
                    priorityQueue.insert(neighbourNode);
                } else{
                    if (!neighbourNode.board.equals(currentNode.previousNode.board)) {
                        priorityQueue.insert(neighbourNode);
                    }
                }
            }
        }
    }

    // min number of moves to solve initial board
    public int moves() {
        return solutionNode.movesToReachBoard;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        Stack<Board> boardSequence = new Stack<>();

        Node currentNode = solutionNode;
        while (currentNode != null) {
            boardSequence.push(currentNode.board);
            currentNode = currentNode.previousNode;
        }

        return boardSequence;
    }

    private int manhattanPriorityFunc(Node node) {
        return node.board.manhattan() + node.movesToReachBoard;
    }

    // test client (see below)
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = 0;
        if (scanner.hasNext()) {
//            int n = Integer.parseInt(scanner.next());
            n = scanner.nextInt();
        }

        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (scanner.hasNext()) {
                    tiles[i][j] = scanner.nextInt();
                }
            }
        }

        Solver puzzleSolver = new Solver(new Board(tiles));
        StdOut.println("Minimum number of moves = " + puzzleSolver.moves());

        Stack<Board> boardSequence = (Stack<Board>) puzzleSolver.solution();
        while (!boardSequence.isEmpty()) {
            StdOut.println(boardSequence.pop().toString());
        }
    }

}