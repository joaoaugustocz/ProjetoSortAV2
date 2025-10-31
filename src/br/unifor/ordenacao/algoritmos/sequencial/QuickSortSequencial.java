package br.unifor.ordenacao.algoritmos.sequencial;

import br.unifor.ordenacao.algoritmos.SortAlgorithm;

/**
 * Quick Sort recursivo clássico com pivô central e partição in-place.
 */
public class QuickSortSequencial implements SortAlgorithm {

    @Override
    public String getNome() {
        return "Quick Sort (Sequencial)";
    }

    @Override
    public void ordenar(int[] dados) {
        quickSort(dados, 0, dados.length - 1);
    }

    private void quickSort(int[] dados, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int pivoIndex = particionar(dados, inicio, fim);
        quickSort(dados, inicio, pivoIndex - 1);
        quickSort(dados, pivoIndex + 1, fim);
    }

    private int particionar(int[] dados, int inicio, int fim) {
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

    private void trocar(int[] dados, int i, int j) {
        int temp = dados[i];
        dados[i] = dados[j];
        dados[j] = temp;
    }
}
