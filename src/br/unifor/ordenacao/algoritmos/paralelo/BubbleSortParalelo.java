package br.unifor.ordenacao.algoritmos.paralelo;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Versão paralela do Bubble Sort utilizando o algoritmo Odd-Even Transposition.
 * A divisão das comparações por bloco permite aproveitar múltiplas threads
 * de forma simples, ainda que a escalabilidade seja limitada.
 */
public class BubbleSortParalelo implements ParallelSortAlgorithm {

    @Override
    public String getNome() {
        return "Bubble Sort (Paralelo)";
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

        int threads = Math.max(1, numThreads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        try {
            int n = dados.length;
            for (int passo = 0; passo < n; passo++) {
                int paridade = passo & 1;
                AtomicBoolean houveTroca = new AtomicBoolean(false);
                CountDownLatch latch = new CountDownLatch(threads);
                int limite = n - 1;
                int totalComparacoes = limite;
                int tamanhoBloco = Math.max(1, totalComparacoes / threads + (totalComparacoes % threads == 0 ? 0 : 1));

                for (int t = 0; t < threads; t++) {
                    int inicio = t * tamanhoBloco;
                    int fimExclusivo = Math.min(totalComparacoes, inicio + tamanhoBloco);
                    pool.submit(() -> {
                        try {
                            int i = alinharIndice(inicio, paridade);
                            while (i < fimExclusivo) {
                                if (dados[i] > dados[i + 1]) {
                                    int temp = dados[i];
                                    dados[i] = dados[i + 1];
                                    dados[i + 1] = temp;
                                    houveTroca.set(true);
                                }
                                i += 2;
                            }
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                aguardarLatch(latch);

                if (!houveTroca.get()) {
                    break;
                }
            }
        } finally {
            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int alinharIndice(int indice, int paridade) {
        int resto = indice & 1;
        if (resto == paridade) {
            return indice;
        }
        return indice + 1;
    }

    private void aguardarLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
