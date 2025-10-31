package br.unifor.ordenacao.util;

/**
 * Utilitário simples para validar se um vetor está ordenado de forma não decrescente.
 */
public final class ValidadorOrdenacao {

    private ValidadorOrdenacao() {
    }

    /**
     * @param dados vetor a ser inspecionado.
     * @return true caso esteja ordenado em ordem crescente.
     */
    public static boolean estaOrdenado(int[] dados) {
        for (int i = 1; i < dados.length; i++) {
            if (dados[i - 1] > dados[i]) {
                return false;
            }
        }
        return true;
    }
}
