package br.unifor.ordenacao.algoritmos;

/**
 * Interface para algoritmos de ordenação que possuem execução paralela.
 */
public interface ParallelSortAlgorithm extends SortAlgorithm {

    /**
     * Ordena o vetor utilizando a quantidade de threads indicada.
     *
     * @param dados       vetor que será ordenado.
     * @param numThreads  número desejado de threads de trabalho.
     */
    void ordenarParalelo(int[] dados, int numThreads);
}
