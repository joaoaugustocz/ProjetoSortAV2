package br.unifor.ordenacao;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;
import br.unifor.ordenacao.algoritmos.SortAlgorithm;
import br.unifor.ordenacao.algoritmos.paralelo.BubbleSortParalelo;
import br.unifor.ordenacao.algoritmos.paralelo.CountingSortParalelo;
import br.unifor.ordenacao.algoritmos.paralelo.MergeSortParalelo;
import br.unifor.ordenacao.algoritmos.paralelo.QuickSortParalelo;
import br.unifor.ordenacao.algoritmos.sequencial.BubbleSortSequencial;
import br.unifor.ordenacao.algoritmos.sequencial.CountingSortSequencial;
import br.unifor.ordenacao.algoritmos.sequencial.MergeSortSequencial;
import br.unifor.ordenacao.algoritmos.sequencial.QuickSortSequencial;
import br.unifor.ordenacao.teste.ExecutorTestesDesempenho;
import br.unifor.ordenacao.teste.ResultadoExecucao;
import br.unifor.ordenacao.util.AlgoritmoDisponivel;
import br.unifor.ordenacao.util.ExportadorCsv;
import br.unifor.ordenacao.util.GeradorDados;
import br.unifor.ordenacao.util.TipoDado;
import br.unifor.ordenacao.visualizacao.ControladorExecucao;
import br.unifor.ordenacao.visualizacao.JanelaMonitoramento;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Ponto de entrada do aplicativo de análise de desempenho interativa.
 */
public class Main {

    private static final Path ARQUIVO_RESULTADOS = Path.of("resultados", "desempenho.csv");

    public static void main(String[] args) {
        List<AlgoritmoDisponivel> algoritmos = criarAlgoritmosDisponiveis();

        GeradorDados geradorDados = new GeradorDados(12345L, -10_000, 90_000);
        ExecutorTestesDesempenho executorDesempenho = new ExecutorTestesDesempenho(geradorDados);
        ExportadorCsv exportadorCsv = new ExportadorCsv();

        List<ResultadoExecucao> historicoResultados = Collections.synchronizedList(new ArrayList<>());
        ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "executor-testes");
            thread.setDaemon(true);
            return thread;
        });

        JanelaMonitoramento janela = iniciarJanela(algoritmos);
        janela.atualizarStatus("Pronto para executar testes.");

        ControladorExecucao controlador = new ControladorExecucao() {
            @Override
            public void executarSequencial(AlgoritmoDisponivel algoritmo,
                                           TipoDado tipoDado,
                                           int tamanho,
                                           int amostras) {
                SortAlgorithm seq = algoritmo.getSequencial();
                if (seq == null) {
                    janela.atualizarStatus("Algoritmo sequencial indisponível.");
                    return;
                }

                janela.atualizarStatus(String.format("Executando %s com %d elementos (%d amostras)...",
                        seq.getNome(), tamanho, amostras));

                pool.submit(() -> {
                    try {
                        List<ResultadoExecucao> resultados = executorDesempenho.executarSequencial(
                                seq,
                                tamanho,
                                tipoDado,
                                amostras,
                                janela::registrarResultado
                        );
                        historicoResultados.addAll(resultados);
                        salvarCsv(exportadorCsv, historicoResultados, janela);
                        janela.atualizarStatus(String.format(
                                "Execução sequencial concluída (%d amostras). CSV atualizado.",
                                resultados.size()));
                    } catch (Exception e) {
                        janela.atualizarStatus("Falha na execução sequencial: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void executarParalelo(AlgoritmoDisponivel algoritmo,
                                         TipoDado tipoDado,
                                         int tamanho,
                                         int amostras,
                                         int threads) {
                ParallelSortAlgorithm par = algoritmo.getParalelo();
                if (par == null) {
                    janela.atualizarStatus("Algoritmo paralelo indisponível.");
                    return;
                }

                janela.atualizarStatus(String.format("Executando %s com %d elementos (%d amostras, %d threads)...",
                        par.getNome(), tamanho, amostras, threads));

                pool.submit(() -> {
                    try {
                        List<ResultadoExecucao> resultados = executorDesempenho.executarParalelo(
                                par,
                                tamanho,
                                tipoDado,
                                amostras,
                                threads,
                                janela::registrarResultado
                        );
                        historicoResultados.addAll(resultados);
                        salvarCsv(exportadorCsv, historicoResultados, janela);
                        janela.atualizarStatus(String.format(
                                "Execução paralela concluída (%d amostras). CSV atualizado.",
                                resultados.size()));
                    } catch (Exception e) {
                        janela.atualizarStatus("Falha na execução paralela: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void executarComparativo(AlgoritmoDisponivel algoritmo,
                                            TipoDado tipoDado,
                                            int tamanho,
                                            int amostras,
                                            int threads) {
                SortAlgorithm seq = algoritmo.getSequencial();
                ParallelSortAlgorithm par = algoritmo.getParalelo();

                if (seq == null && par == null) {
                    janela.atualizarStatus("Nenhuma versão disponível para comparação.");
                    return;
                }

                janela.atualizarStatus(String.format(
                        "Executando comparação %s (seq/par) com %d elementos (%d amostras, %d threads)...",
                        algoritmo.getNomeBase(), tamanho, amostras, threads));

                pool.submit(() -> {
                    try {
                        List<ResultadoExecucao> resultados = executorDesempenho.executarComparativo(
                                seq,
                                par,
                                tamanho,
                                tipoDado,
                                amostras,
                                threads,
                                janela::registrarResultado
                        );
                        historicoResultados.addAll(resultados);
                        salvarCsv(exportadorCsv, historicoResultados, janela);
                        janela.atualizarStatus(String.format(
                                "Comparação concluída (%d execuções registradas). CSV atualizado.",
                                resultados.size()));
                    } catch (Exception e) {
                        janela.atualizarStatus("Falha na comparação: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void limparHistorico() {
                pool.submit(() -> {
                    try {
                        synchronized (historicoResultados) {
                            historicoResultados.clear();
                        }
                        salvarCsv(exportadorCsv, historicoResultados, janela);
                        janela.atualizarStatus("Histórico limpo. CSV reiniciado.");
                    } catch (Exception e) {
                        janela.atualizarStatus("Falha ao limpar histórico: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        };

        janela.configurarControlador(controlador);

        Runtime.getRuntime().addShutdownHook(new Thread(pool::shutdownNow));
    }

    private static List<AlgoritmoDisponivel> criarAlgoritmosDisponiveis() {
        return List.of(
                new AlgoritmoDisponivel("Bubble Sort", new BubbleSortSequencial(), new BubbleSortParalelo()),
                new AlgoritmoDisponivel("Quick Sort", new QuickSortSequencial(), new QuickSortParalelo()),
                new AlgoritmoDisponivel("Merge Sort", new MergeSortSequencial(), new MergeSortParalelo()),
                new AlgoritmoDisponivel("Counting Sort", new CountingSortSequencial(), new CountingSortParalelo())
        );
    }

    private static JanelaMonitoramento iniciarJanela(List<AlgoritmoDisponivel> algoritmos) {
        AtomicReference<JanelaMonitoramento> referencia = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            JanelaMonitoramento janela = new JanelaMonitoramento(algoritmos);
            referencia.set(janela);
            janela.setVisible(true);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return referencia.get();
    }

    private static void salvarCsv(ExportadorCsv exportador,
                                  List<ResultadoExecucao> resultados,
                                  JanelaMonitoramento janela) {
        List<ResultadoExecucao> copia;
        synchronized (resultados) {
            copia = new ArrayList<>(resultados);
        }

        try {
            exportador.salvar(ARQUIVO_RESULTADOS, copia);
        } catch (IOException e) {
            janela.atualizarStatus("Falha ao salvar CSV: " + e.getMessage());
        }
    }
}
