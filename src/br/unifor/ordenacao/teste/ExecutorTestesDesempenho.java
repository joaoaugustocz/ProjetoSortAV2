package br.unifor.ordenacao.teste;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;
import br.unifor.ordenacao.algoritmos.SortAlgorithm;
import br.unifor.ordenacao.util.GeradorDados;
import br.unifor.ordenacao.util.TipoDado;
import br.unifor.ordenacao.util.ValidadorOrdenacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Responsável por medir o desempenho dos algoritmos conforme os parâmetros escolhidos.
 */
public class ExecutorTestesDesempenho {

    private final GeradorDados geradorDados;

    public ExecutorTestesDesempenho(GeradorDados geradorDados) {
        this.geradorDados = geradorDados;
    }

    /**
     * Executa apenas a versão sequencial do algoritmo informado.
     */
    public List<ResultadoExecucao> executarSequencial(SortAlgorithm algoritmo,
                                                      int tamanho,
                                                      TipoDado tipo,
                                                      int amostras,
                                                      Consumer<ResultadoExecucao> callback) {
        List<int[]> amostrasBase = gerarAmostrasBase(tamanho, tipo, amostras);
        List<ResultadoExecucao> resultados = new ArrayList<>();

        for (int amostra = 0; amostra < amostras; amostra++) {
            int[] dados = Arrays.copyOf(amostrasBase.get(amostra), tamanho);
            ResultadoExecucao resultado = medirExecucaoSequencial(algoritmo, dados, tamanho, tipo, amostra);
            resultados.add(resultado);
            notificar(callback, resultado);
        }

        return resultados;
    }

    /**
     * Executa apenas a versão paralela do algoritmo informado.
     */
    public List<ResultadoExecucao> executarParalelo(ParallelSortAlgorithm algoritmo,
                                                    int tamanho,
                                                    TipoDado tipo,
                                                    int amostras,
                                                    int threads,
                                                    Consumer<ResultadoExecucao> callback) {
        List<int[]> amostrasBase = gerarAmostrasBase(tamanho, tipo, amostras);
        List<ResultadoExecucao> resultados = new ArrayList<>();

        for (int amostra = 0; amostra < amostras; amostra++) {
            int[] dados = Arrays.copyOf(amostrasBase.get(amostra), tamanho);
            ResultadoExecucao resultado = medirExecucaoParalela(algoritmo, dados, tamanho, tipo, amostra, threads);
            resultados.add(resultado);
            notificar(callback, resultado);
        }

        return resultados;
    }

    /**
     * Executa sequencial e paralelo compartilhando as mesmas amostras para comparação direta.
     */
    public List<ResultadoExecucao> executarComparativo(SortAlgorithm sequencial,
                                                       ParallelSortAlgorithm paralelo,
                                                       int tamanho,
                                                       TipoDado tipo,
                                                       int amostras,
                                                       int threads,
                                                       Consumer<ResultadoExecucao> callback) {
        List<int[]> amostrasBase = gerarAmostrasBase(tamanho, tipo, amostras);
        List<ResultadoExecucao> resultados = new ArrayList<>();

        if (sequencial != null) {
            for (int amostra = 0; amostra < amostras; amostra++) {
                int[] dados = Arrays.copyOf(amostrasBase.get(amostra), tamanho);
                ResultadoExecucao resultado = medirExecucaoSequencial(sequencial, dados, tamanho, tipo, amostra);
                resultados.add(resultado);
                notificar(callback, resultado);
            }
        }

        if (paralelo != null) {
            for (int amostra = 0; amostra < amostras; amostra++) {
                int[] dados = Arrays.copyOf(amostrasBase.get(amostra), tamanho);
                ResultadoExecucao resultado = medirExecucaoParalela(paralelo, dados, tamanho, tipo, amostra, threads);
                resultados.add(resultado);
                notificar(callback, resultado);
            }
        }

        return resultados;
    }

    private List<int[]> gerarAmostrasBase(int tamanho, TipoDado tipo, int amostras) {
        List<int[]> base = new ArrayList<>(amostras);
        for (int i = 0; i < amostras; i++) {
            base.add(geradorDados.gerar(tamanho, tipo));
        }
        return base;
    }

    private ResultadoExecucao medirExecucaoSequencial(SortAlgorithm algoritmo,
                                                      int[] dados,
                                                      int tamanho,
                                                      TipoDado tipo,
                                                      int amostra) {
        long inicio = System.nanoTime();
        String observacao = null;
        boolean sucesso;

        try {
            algoritmo.ordenar(dados);
            sucesso = ValidadorOrdenacao.estaOrdenado(dados);
            if (!sucesso) {
                observacao = "Resultado incorreto detectado pelo validador.";
            }
        } catch (Exception e) {
            sucesso = false;
            observacao = "Erro: " + e.getMessage();
        }

        long duracao = System.nanoTime() - inicio;
        long duracaoMillis = TimeUnit.NANOSECONDS.toMillis(duracao);

        return new ResultadoExecucao(
                algoritmo.getNome(),
                "Sequencial",
                tamanho,
                tipo,
                amostra + 1,
                1,
                duracaoMillis,
                sucesso,
                observacao
        );
    }

    private ResultadoExecucao medirExecucaoParalela(ParallelSortAlgorithm algoritmo,
                                                    int[] dados,
                                                    int tamanho,
                                                    TipoDado tipo,
                                                    int amostra,
                                                    int threads) {
        long inicio = System.nanoTime();
        String observacao = null;
        boolean sucesso;

        try {
            algoritmo.ordenarParalelo(dados, threads);
            sucesso = ValidadorOrdenacao.estaOrdenado(dados);
            if (!sucesso) {
                observacao = "Resultado incorreto detectado pelo validador.";
            }
        } catch (Exception e) {
            sucesso = false;
            observacao = "Erro: " + e.getMessage();
        }

        long duracao = System.nanoTime() - inicio;
        long duracaoMillis = TimeUnit.NANOSECONDS.toMillis(duracao);

        return new ResultadoExecucao(
                algoritmo.getNome(),
                "Paralelo",
                tamanho,
                tipo,
                amostra + 1,
                threads,
                duracaoMillis,
                sucesso,
                observacao
        );
    }

    private void notificar(Consumer<ResultadoExecucao> callback, ResultadoExecucao resultado) {
        if (callback != null) {
            callback.accept(resultado);
        }
    }
}
