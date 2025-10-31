package br.unifor.ordenacao.algoritmos.sequencial;

import br.unifor.ordenacao.algoritmos.SortAlgorithm;

/**
 * Merge Sort clássico utilizando vetor auxiliar.
 */
public class MergeSortSequencial implements SortAlgorithm {

    @Override
    public String getNome() {
        return "Merge Sort (Sequencial)";
    }

    @Override
    public void ordenar(int[] dados) {
        int[] auxiliar = new int[dados.length];
        mergeSort(dados, auxiliar, 0, dados.length - 1);
    }

    private void mergeSort(int[] dados, int[] auxiliar, int inicio, int fim) {
        if (inicio >= fim) {
            return;
        }

        int meio = (inicio + fim) >>> 1;
        mergeSort(dados, auxiliar, inicio, meio);
        mergeSort(dados, auxiliar, meio + 1, fim);
        intercalar(dados, auxiliar, inicio, meio, fim);
    }

    private void intercalar(int[] dados, int[] auxiliar, int inicio, int meio, int fim) {
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
