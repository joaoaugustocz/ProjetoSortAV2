package br.unifor.ordenacao.algoritmos.sequencial;

import br.unifor.ordenacao.algoritmos.SortAlgorithm;

/**
 * Implementação tradicional do Bubble Sort.
 * Ideal para fins didáticos, apesar do alto custo O(n^2).
 */
public class BubbleSortSequencial implements SortAlgorithm {

    @Override
    public String getNome() {
        return "Bubble Sort (Sequencial)";
    }

    @Override
    public void ordenar(int[] dados) {
        boolean houveTroca;
        int n = dados.length;

        do {
            houveTroca = false;

            for (int i = 0; i < n - 1; i++) {
                if (dados[i] > dados[i + 1]) {
                    int temp = dados[i];
                    dados[i] = dados[i + 1];
                    dados[i + 1] = temp;
                    houveTroca = true;
                }
            }

            n--; // Último elemento já está no lugar correto.
        } while (houveTroca);
    }
}
