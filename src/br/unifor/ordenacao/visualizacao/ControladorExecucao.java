package br.unifor.ordenacao.visualizacao;

import br.unifor.ordenacao.util.AlgoritmoDisponivel;
import br.unifor.ordenacao.util.TipoDado;

/**
 * Define as ações disponíveis a partir dos controles da interface gráfica.
 */
public interface ControladorExecucao {

    void executarSequencial(AlgoritmoDisponivel algoritmo,
                             TipoDado tipoDado,
                             int tamanho,
                             int amostras);

    void executarParalelo(AlgoritmoDisponivel algoritmo,
                          TipoDado tipoDado,
                          int tamanho,
                          int amostras,
                          int threads);

    void executarComparativo(AlgoritmoDisponivel algoritmo,
                             TipoDado tipoDado,
                             int tamanho,
                             int amostras,
                             int threads);

    /**
     * Limpa histórico de execuções e arquivos relacionados.
     */
    void limparHistorico();
}
