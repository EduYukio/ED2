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
        -Função que ordena string:
            https://www.geeksforgeeks.org/sort-a-string-in-java-2-different-ways/

        -Receita para construir uma classe iterável:
            https://www.ime.usp.br/~coelho/mac0323-2019/aulas/aula01/slides.pdf

        -Algoritmo de geração de permutações em ordem lexicográfica que lida com repetições em O(n) de espaço e tempo
 (Narayana Pandita):
            https://en.wikipedia.org/wiki/Permutation#Generation_in_lexicographic_order


    Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:

****************************************************************/

// excessões pedidas
import java.lang.IllegalArgumentException;
import java.lang.UnsupportedOperationException;
import java.util.NoSuchElementException;
import java.util.Arrays;

import java.util.Iterator; // passo 0 para criarmos um iterador

import edu.princeton.cs.algs4.StdOut;

public class Arrangements implements Iterable<String> {
    private String sortedString;
    private int strLen;

    // constructor
    private Arrangements(String s) {
        if(s == null){
            throw new IllegalArgumentException();
        }
        this.sortedString = sortString(s);
        this.strLen = s.length();
    }

    // sort a string
    private static String sortString(String inputString){
        char[] tempArray = inputString.toCharArray();
        Arrays.sort(tempArray);
        return new String(tempArray);
    }

    // returns a new iterator object
    public Iterator<String> iterator() {
        return new ArrangementIterator();
    }

    // this code utilizes the algorithm created by Narayana Pandita, adapted to work on the iterator format
    private class ArrangementIterator implements Iterator<String>{
        // k is the largest index of the array such that array[k] < array[k+1]
        int k = -1;
        char[] charArray = sortedString.toCharArray();
        boolean alreadyPrintedTheFirstOne = false;

        // verify if can generate another permutation, based on the last generated
        public boolean hasNext(){
            for(int i = strLen - 2; i >= 0; i--){
                if(charArray[i] < charArray[i+1]){
                    k = i;
                    return true;
                }
            }

            charArray = sortedString.toCharArray();
            alreadyPrintedTheFirstOne = false;
            return false;
        }

        // generates the next permutation
        public String next(){
            if(!hasNext() || k < 0){
                throw new NoSuchElementException();
            }

            if(!alreadyPrintedTheFirstOne){
                alreadyPrintedTheFirstOne = true;
                return sortedString;
            }

            // l is the largest index of the array, greater than k, such that array[k] < array[l]
            int l = -1;
            for(int i = strLen - 1; i > k; i--){
                if(charArray[k] < charArray[i]){
                    l = i;
                    break;
                }
            }
            if(l < 0){
                throw new NoSuchElementException();
            }
            swap(charArray, k, l);

            int j = strLen - 1;
            for(int i = k + 1; j > i; i++){
                swap(charArray, i, j);
                j--;
            }

            k = -1;
            return String.valueOf(charArray);
        }

        public void remove(){
            throw new UnsupportedOperationException();
        }
    }

    // swap two letters from a char array
    private static void swap(char[] charArray, int i, int j)
    {
        char temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;
    }

    // Unit test
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        String s = args[0];
        Arrangements arr = new Arrangements(s);

        StdOut.println("Teste 1: imprime no máximo os 10 primeiros arranjos");
        Iterator<String> it = arr.iterator();
        for (int i = 0; it.hasNext() && i < 10; i++) {
            StdOut.println(i + " : " + it.next());
        }

        StdOut.println("Teste 2: imprime todos os arranjos");
        int i = 0;
        for (String arranjo: arr) {
            StdOut.println(i + " : " + arranjo);
            i++;
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;

        StdOut.println("\nTempo decorrido em ms: " + duration);
    }
}
