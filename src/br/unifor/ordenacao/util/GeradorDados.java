package br.unifor.ordenacao.util;

import java.util.Random;

/**
 * Responsável por produzir vetores inteiros com diferentes características.
 */
public class GeradorDados {

    private final Random random;
    private final int limiteInferior;
    private final int limiteSuperior;

    /**
     * @param seed            semente para reprodutibilidade.
     * @param limiteInferior  menor valor possível.
     * @param limiteSuperior  maior valor possível.
     */
    public GeradorDados(long seed, int limiteInferior, int limiteSuperior) {
        this.random = new Random(seed);
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Gera um novo vetor com base no tipo desejado.
     *
     * @param tamanho quantidade de elementos.
     * @param tipo    perfil de organização inicial.
     * @return vetor recém criado.
     */
    public int[] gerar(int tamanho, TipoDado tipo) {
        int[] dados = new int[tamanho];

        switch (tipo) {
            case ALEATORIO:
                preencherAleatorio(dados);
                break;
            case ORDENADO:
                preencherOrdenado(dados);
                break;
            case REVERSO:
                preencherReverso(dados);
                break;
            case QUASE_ORDENADO:
                preencherQuaseOrdenado(dados);
                break;
            default:
                preencherAleatorio(dados);
        }

        return dados;
    }

    private void preencherAleatorio(int[] dados) {
        int alcance = limiteSuperior - limiteInferior + 1;
        for (int i = 0; i < dados.length; i++) {
            dados[i] = limiteInferior + random.nextInt(alcance);
        }
    }

    private void preencherOrdenado(int[] dados) {
        preencherAleatorio(dados);
        java.util.Arrays.sort(dados);
    }

    private void preencherReverso(int[] dados) {
        preencherOrdenado(dados);
        for (int i = 0, j = dados.length - 1; i < j; i++, j--) {
            int temp = dados[i];
            dados[i] = dados[j];
            dados[j] = temp;
        }
    }

    private void preencherQuaseOrdenado(int[] dados) {
        preencherOrdenado(dados);
        int quantidadeTrocas = Math.max(1, dados.length / 20);
        for (int i = 0; i < quantidadeTrocas; i++) {
            int a = random.nextInt(dados.length);
            int b = random.nextInt(dados.length);
            int temp = dados[a];
            dados[a] = dados[b];
            dados[b] = temp;
        }
    }
}
