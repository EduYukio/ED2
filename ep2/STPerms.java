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
    -Algoritmo de permutação (adaptado):
        -Descrição do EP2 no paca, seção "Inspiração"
        https://www.ime.usp.br/~coelho/mac0323-2019/eps/ep02/

    -Função "hasSubsequencesLargerThanMax()"
        -A monitora Beatriz e o professor Coelho me sugeriram a ideia central aplicada nesta função.


 Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
 ****************************************************************/

/******************************************************************************
 *  Compilation:  javac-algs4 STPerms.java
 *  Execution:    java STPerms n s t opcao
 *
 *  Enumera todas as (s,t)-permutações das n primeiras letras do alfabeto.
 *  As permutações devem ser exibidas em ordem lexicográfica.
 *  Sobre o papel da opcao, leia o enunciado do EP.
 *
 *  % java STPerms 4 2 2 0
 *  badc
 *  bdac
 *  cadb
 *  cdab
 *  4
 *  % java STPerms 4 2 2 1
 *  4
 *  % java STPerms 4 2 2 2
 *  badc
 *  bdac
 *  cadb
 *  cdab
 *  4
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;

public class STPerms {
    private static int n;
    private static int s;
    private static int t;
    private static int option;
    private static int count = 0;
    private static int[] maxAscendingArray;
    private static int[] maxDescendingArray;

    // find permutations of the characters of the string
    // and print all of the (s,t)-permutations
    private static void findSTPermutations(String string) {
        findSTPermutations("", string);
    }
    private static void findSTPermutations(String prefix, String string) {
        int len = string.length();

        if(hasSubsequenceLargerThanMax(prefix, s, true)){
            return;
        }

        if(hasSubsequenceLargerThanMax(prefix, t, false)){
            return;
        }

        if (len == 0) {
            printAndCount(prefix);
        } else {
            for (int i = 0; i < len; i++)
                findSTPermutations(prefix + string.charAt(i), string.substring(0, i) + string.substring(i + 1, len));
        }
    }

    // check if the string has any ascending subsequences larger than max or
    // any descending subsequences larger than max
    private static boolean hasSubsequenceLargerThanMax(String string, int max, boolean ascendingOrder){
        int stringLen = string.length();
        if(stringLen == 0){
            return false;
        }

        int lastLetterIndex = stringLen - 1;
        char lastLetter = string.charAt(lastLetterIndex);
        int maxSubLen = 0;

        int[] chosenArray = maxAscendingArray;

        if(!ascendingOrder){
            chosenArray = maxDescendingArray;
        }

        for(int i = stringLen - 2; i >= 0; i--){
            if(chosenArray[i] > maxSubLen){
                if(ascendingOrder){
                    if(string.charAt(i) < lastLetter){
                        maxSubLen = chosenArray[i];
                    }
                }
                else{
                    if(string.charAt(i) > lastLetter){
                        maxSubLen = chosenArray[i];
                    }
                }
            }
        }
        if(maxSubLen + 1 > max){
            return true;
        }

        chosenArray[lastLetterIndex] = maxSubLen + 1;

        return false;
    }

    // print the string and increment the count
    private static void printAndCount(String string){
        if (option != 1) {
            StdOut.println(string);
        }
        count++;
    }

    public static void main(String[] args) {
        n = Integer.parseInt(args[0]);
        s = Integer.parseInt(args[1]);
        t = Integer.parseInt(args[2]);
        option = Integer.parseInt(args[3]);

        if (n < 1 || n > 26) {
            StdOut.println("Valor de n inválido. Por favor, insira um n inteiro entre 1 e 26.");
            return;
        }

        if (option < 0 || option > 2) {
            StdOut.println("Valor de option inválido. Por favor, insira um número inteiro entre 0 e 2.");
            return;
        }

        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String substring = alphabet.substring(0, n);

        maxAscendingArray = new int[n];
        maxDescendingArray = new int[n];

        long startTime = System.nanoTime();

        findSTPermutations(substring);
        if(option != 0){
            StdOut.println(count);
        }

        long endTime = System.nanoTime();

        StdOut.println("\nTempo decorrido em ms: "+(endTime - startTime)/1000000);
    }
}
