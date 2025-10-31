package br.unifor.ordenacao.algoritmos.paralelo;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Counting Sort paralelo que divide o vetor original em blocos independentes
 * para cálculo das frequências, consolidando o resultado ao final.
 */
public class CountingSortParalelo implements ParallelSortAlgorithm {

    @Override
    public String getNome() {
        return "Counting Sort (Paralelo)";
    }

    @Override
    public void ordenar(int[] dados) {
        ordenarParalelo(dados, Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void ordenarParalelo(int[] dados, int numThreads) {
        if (dados.length < 2) {
            return;
        }

        int menor = dados[0];
        int maior = dados[0];

        for (int valor : dados) {
            if (valor < menor) {
                menor = valor;
            }
            if (valor > maior) {
                maior = valor;
            }
        }

        int alcance = maior - menor + 1;
        int threads = Math.max(1, numThreads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        int[][] contagensParciais = new int[threads][alcance];
        final int deslocamento = menor;
        int tamanhoBloco = Math.max(1, dados.length / threads + (dados.length % threads == 0 ? 0 : 1));
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            int indiceThread = t;
            int inicio = t * tamanhoBloco;
            int fimExclusivo = Math.min(dados.length, inicio + tamanhoBloco);

            pool.submit(() -> {
                try {
                    for (int i = inicio; i < fimExclusivo; i++) {
                        contagensParciais[indiceThread][dados[i] - deslocamento]++;
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        aguardar(latch);
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] contagemFinal = new int[alcance];
        for (int t = 0; t < threads; t++) {
            for (int i = 0; i < alcance; i++) {
                contagemFinal[i] += contagensParciais[t][i];
            }
        }

        int indice = 0;
        for (int i = 0; i < alcance; i++) {
            int repeticoes = contagemFinal[i];
            while (repeticoes-- > 0) {
                dados[indice++] = i + menor;
            }
        }
    }

    private void aguardar(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
