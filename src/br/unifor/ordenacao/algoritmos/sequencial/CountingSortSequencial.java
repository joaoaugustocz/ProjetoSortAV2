package br.unifor.ordenacao.algoritmos.sequencial;

import br.unifor.ordenacao.algoritmos.SortAlgorithm;

/**
 * Counting Sort sequencial para dados inteiros dentro de uma faixa conhecida.
 */
public class CountingSortSequencial implements SortAlgorithm {

    @Override
    public String getNome() {
        return "Counting Sort (Sequencial)";
    }

    @Override
    public void ordenar(int[] dados) {
        if (dados.length == 0) {
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
        int[] contagem = new int[alcance];

        for (int valor : dados) {
            contagem[valor - menor]++;
        }

        int indice = 0;
        for (int i = 0; i < alcance; i++) {
            int frequencia = contagem[i];
            while (frequencia-- > 0) {
                dados[indice++] = i + menor;
            }
        }
    }
}
