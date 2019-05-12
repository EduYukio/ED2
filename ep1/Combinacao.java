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

/******************************************************************************
 *  Compilation:  javac-algs4 Combinacao.java
 *  Execution:    java Combinacao n k [option]
 *
 *  Enumera todas as combinações dos números em {1,2,...,n} k a k.
 *  Se option = 0 (default), gera e exibe todas as permutações em ordem
 *  lexicográfica
 *  Se option = 1 apenas, __gera todas__ as combinações, mas __não__ as
 *  exibe; apenas exibe o total de combinações.
 *
 * % java Combinacao 5 3 1
 * 10
 * elapsed time = 0.002
 * % java Combinacao 5 3
 * 1 2 3 
 * 1 2 4 
 * 1 2 5 
 * 1 3 4 
 * 1 3 5 
 * 1 4 5 
 * 2 3 4 
 * 2 3 5 
 * 2 4 5 
 * 3 4 5 
 * 10
 * elapsed time = 0.002
 * % java Combinacao 100 3 1
 * 161700
 * elapsed time = 0.004
 * % java Combinacao 1000 3 1
 * 166167000
 * elapsed time = 0.726
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class Combinacao {
    private static int count = 0; // contador de combinações
    private static int option = 0;

    private static int[] combinationArray;
    private static int currentIndex = 0;
    private static int currentNumber = 1;

    private static String arrayState;
    private static boolean finished = false;

    private static void combination(int n, int k) {
        combinationArray = new int[k];

        fillArrayWithIncrements(k);

        while (!finished) {
            switch (arrayState) {
                case "analyseTheLastOne":
                    sumTheLastOneIfPossible(n);
                    break;

                case "analysePreviousIndex":
                    sumPreviousIfPossible(k);
                    break;
            }
        }
    }

    private static void fillArrayWithIncrements(int k) {
        combinationArray[currentIndex] = currentNumber;

        while (currentIndex + 1 != k) {
            currentNumber++;
            currentIndex++;
            combinationArray[currentIndex] = currentNumber;
        }

        printAndCount();
        arrayState = "analyseTheLastOne";
    }

    private static void sumTheLastOneIfPossible(int n) {
        currentNumber = combinationArray[currentIndex] + 1;
        if (currentNumber <= n) {
            combinationArray[currentIndex] = currentNumber;
            printAndCount();
            arrayState = "analyseTheLastOne";
        } else {
            arrayState = "analysePreviousIndex";
        }
    }

    private static void printAndCount() {
        if (option == 0) {
            for (int number : combinationArray) {
                StdOut.print(number + " ");
            }
            StdOut.println("");
        }
        count++;
    }

    private static void sumPreviousIfPossible(int k) {
        currentIndex--;

        if (currentIndex < 0) {
            finished = true;
            return;
        }

        currentNumber = combinationArray[currentIndex] + 1;

        if (currentNumber == combinationArray[currentIndex + 1]) {
            arrayState = "analysePreviousIndex";
        } else {
            fillArrayWithIncrements(k);
        }
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        if (args.length == 3) {
            option = Integer.parseInt(args[2]);
        }

        Stopwatch timer = new Stopwatch();
        combination(n, k);
        StdOut.println(count);
        StdOut.println("elapsed time = " + timer.elapsedTime());
    }
}
