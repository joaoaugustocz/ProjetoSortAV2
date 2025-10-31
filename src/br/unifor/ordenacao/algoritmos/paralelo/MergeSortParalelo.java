package br.unifor.ordenacao.algoritmos.paralelo;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Merge Sort paralelizado com ForkJoinPool, dividindo o vetor recursivamente.
 */
public class MergeSortParalelo implements ParallelSortAlgorithm {

    private static final int TAMANHO_MINIMO_SUBPROBLEMA = 20_000;

    @Override
    public String getNome() {
        return "Merge Sort (Paralelo)";
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
        int[] auxiliar = new int[dados.length];
        try {
            pool.invoke(new MergeSortTask(dados, auxiliar, 0, dados.length - 1));
        } finally {
            pool.shutdown();
        }
    }

    private static class MergeSortTask extends RecursiveAction {
        private final int[] dados;
        private final int[] auxiliar;
        private final int inicio;
        private final int fim;

        MergeSortTask(int[] dados, int[] auxiliar, int inicio, int fim) {
            this.dados = dados;
            this.auxiliar = auxiliar;
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void compute() {
            if (inicio >= fim) {
                return;
            }

            if (fim - inicio < TAMANHO_MINIMO_SUBPROBLEMA) {
                mergeSortSequencial(dados, auxiliar, inicio, fim);
                return;
            }

            int meio = (inicio + fim) >>> 1;
            MergeSortTask esquerda = new MergeSortTask(dados, auxiliar, inicio, meio);
            MergeSortTask direita = new MergeSortTask(dados, auxiliar, meio + 1, fim);
            invokeAll(esquerda, direita);
            intercalar(dados, auxiliar, inicio, meio, fim);
        }
    }

    private static void mergeSortSequencial(int[] dados, int[] auxiliar, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int meio = (inicio + fim) >>> 1;
        mergeSortSequencial(dados, auxiliar, inicio, meio);
        mergeSortSequencial(dados, auxiliar, meio + 1, fim);
        intercalar(dados, auxiliar, inicio, meio, fim);
    }

    private static void intercalar(int[] dados, int[] auxiliar, int inicio, int meio, int fim) {
        System.arraycopy(dados, inicio, auxiliar, inicio, fim - inicio + 1);

        int i = inicio;
        int j = meio + 1;
        int k = inicio;

        while (i <= meio && j <= fim) {
            if (auxiliar[i] <= auxiliar[j]) {
                dados[k++] = auxiliar[i++];
            } else {
                dados[k++] = auxiliar[j++];
            }
        }

        while (i <= meio) {
            dados[k++] = auxiliar[i++];
        }

        while (j <= fim) {
            dados[k++] = auxiliar[j++];
        }
    }
}
