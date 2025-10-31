package br.unifor.ordenacao.algoritmos;

/**
 * Interface base para algoritmos de ordenação sequenciais.
 * Cada implementação deve ordenar o vetor recebido in-place.
 */
public interface SortAlgorithm {

    /**
     * @return Nome legível do algoritmo, usado em relatórios e gráficos.
     */
    String getNome();

    /**
     * Ordena o vetor no lugar.
     *
     * @param dados vetor que será ordenado.
     */
    void ordenar(int[] dados);
}
