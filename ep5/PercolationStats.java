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

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.lang.IllegalArgumentException;
import java.util.Random;

public class PercolationStats {
    private double[] fractionArray;
    private int trials;
    private double alreadyCalculatedMean = -1;
    private double alreadyCalculatedStddev = -1;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        Random randomNumberGenerator = new Random();
        fractionArray = new double[trials];
        this.trials = trials;

        for (int i = 0; i < trials; i++) {
            Percolation newTrial = new Percolation(n);
            int x, y;
            while (!newTrial.percolates()) {
                do {
                    x = randomNumberGenerator.nextInt(n);
                    y = randomNumberGenerator.nextInt(n);
                } while (newTrial.isOpen(x, y));
                newTrial.open(x, y);
            }

            fractionArray[i] = newTrial.numberOfOpenSites() / (double) (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        if (this.alreadyCalculatedMean < 0) {
            double sum = 0;
            for (double fraction : fractionArray) {
                sum += fraction;
            }
            this.alreadyCalculatedMean = sum / trials;
        }

        return this.alreadyCalculatedMean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (this.alreadyCalculatedStddev < 0) {
            double mean = mean();

            double sum = 0;
            for (double fraction : fractionArray) {
                sum += Math.pow(fraction - mean, 2);
            }

            this.alreadyCalculatedStddev = Math.sqrt(sum / (trials - 1));
        }

        return this.alreadyCalculatedStddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        return mean() - (1.96 * stddev() / Math.sqrt(trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        return mean() + (1.96 * stddev() / Math.sqrt(trials));
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        Stopwatch stopwatch = new Stopwatch();

        PercolationStats newStats = new PercolationStats(n, T);

        StdOut.println("mean()           = " + newStats.mean());
        StdOut.println("stddev()         = " + newStats.stddev());
        StdOut.println("confidenceLow()  = " + newStats.confidenceLow());
        StdOut.println("confidenceHigh() = " + newStats.confidenceHigh());
        StdOut.println("elapsed time     = " + stopwatch.elapsedTime());
    }
}