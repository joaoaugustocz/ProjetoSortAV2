package br.unifor.ordenacao.teste;

import br.unifor.ordenacao.util.TipoDado;

/**
 * Representa o registro de uma execução individual feita durante os testes.
 */
public class ResultadoExecucao {

    private final String algoritmo;
    private final String modo;
    private final int tamanho;
    private final TipoDado tipoDado;
    private final int amostra;
    private final int threads;
    private final long tempoMillis;
    private final boolean sucesso;
    private final String observacao;

    public ResultadoExecucao(String algoritmo,
                             String modo,
                             int tamanho,
                             TipoDado tipoDado,
                             int amostra,
                             int threads,
                             long tempoMillis,
                             boolean sucesso,
                             String observacao) {
        this.algoritmo = algoritmo;
        this.modo = modo;
        this.tamanho = tamanho;
        this.tipoDado = tipoDado;
        this.amostra = amostra;
        this.threads = threads;
        this.tempoMillis = tempoMillis;
        this.sucesso = sucesso;
        this.observacao = observacao;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public String getModo() {
        return modo;
    }

    public int getTamanho() {
        return tamanho;
    }

    public TipoDado getTipoDado() {
        return tipoDado;
    }

    public int getAmostra() {
        return amostra;
    }

    public int getThreads() {
        return threads;
    }

    public long getTempoMillis() {
        return tempoMillis;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public String getObservacao() {
        return observacao;
    }

    public String chaveAgrupamento() {
        if ("Sequencial".equals(modo)) {
            return algoritmo + " - " + modo;
        }
        return algoritmo + " - " + modo + " (" + threads + " threads)";
    }
}
