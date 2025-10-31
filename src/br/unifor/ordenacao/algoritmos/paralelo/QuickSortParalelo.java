package br.unifor.ordenacao.algoritmos.paralelo;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Quick Sort implementado sobre o framework ForkJoin para paralelização recursiva.
 */
public class QuickSortParalelo implements ParallelSortAlgorithm {

    private static final int TAMANHO_MINIMO_SUBPROBLEMA = 10_000;

    @Override
    public String getNome() {
        return "Quick Sort (Paralelo)";
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

        ForkJoinPool pool = new ForkJoinPool(Math.max(1, numThreads));
        try {
            pool.invoke(new QuickSortTask(dados, 0, dados.length - 1));
        } finally {
            pool.shutdown();
        }
    }

    private static class QuickSortTask extends RecursiveAction {
        private final int[] dados;
        private final int inicio;
        private final int fim;

        QuickSortTask(int[] dados, int inicio, int fim) {
            this.dados = dados;
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void compute() {
            if (inicio >= fim) {
                return;
            }

            if (fim - inicio < TAMANHO_MINIMO_SUBPROBLEMA) {
                quickSortSequencial(dados, inicio, fim);
                return;
            }

            int pivoIndex = particionar(dados, inicio, fim);
            QuickSortTask esquerda = new QuickSortTask(dados, inicio, pivoIndex - 1);
            QuickSortTask direita = new QuickSortTask(dados, pivoIndex + 1, fim);
            invokeAll(esquerda, direita);
        }
    }

    private static void quickSortSequencial(int[] dados, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int pivoIndex = particionar(dados, inicio, fim);
        quickSortSequencial(dados, inicio, pivoIndex - 1);
        quickSortSequencial(dados, pivoIndex + 1, fim);
    }

    private static int particionar(int[] dados, int inicio, int fim) {
        int pivo = dados[fim];
        int i = inicio - 1;

        for (int j = inicio; j < fim; j++) {
            if (dados[j] <= pivo) {
                i++;
                trocar(dados, i, j);
            }
        }

        trocar(dados, i + 1, fim);
        return i + 1;
    }

    private static void trocar(int[] dados, int i, int j) {
        int temp = dados[i];
        dados[i] = dados[j];
        dados[j] = temp;
    }
}
