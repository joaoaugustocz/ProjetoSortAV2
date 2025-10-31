package br.unifor.ordenacao.visualizacao;

import br.unifor.ordenacao.teste.ResultadoExecucao;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Modelo de tabela que mantém um buffer com as execuções mais recentes.
 */
public class ModeloTabelaResultados extends AbstractTableModel {

    private static final int LIMITE_REGISTROS = 200;

    private final String[] colunas = {
            "Algoritmo",
            "Modo",
            "Threads",
            "Tipo",
            "Tamanho",
            "Amostra",
            "Tempo (ms)",
            "Sucesso"
    };

    private final Deque<ResultadoExecucao> registros = new ArrayDeque<>();

    public void adicionarResultado(ResultadoExecucao resultado) {
        if (registros.size() == LIMITE_REGISTROS) {
            registros.removeFirst();
        }
        registros.addLast(resultado);
        fireTableDataChanged();
    }

    /**
     * Esvazia a lista de registros exibidos.
     */
    public void limpar() {
        if (!registros.isEmpty()) {
            registros.clear();
            fireTableDataChanged();
        }
    }

    @Override
    public int getRowCount() {
        return registros.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ResultadoExecucao resultado = obterPorIndice(rowIndex);
        if (resultado == null) {
            return "";
        }

        switch (columnIndex) {
            case 0:
                return resultado.getAlgoritmo();
            case 1:
                return resultado.getModo();
            case 2:
                return resultado.getThreads();
            case 3:
                return resultado.getTipoDado().getDescricao();
            case 4:
                return resultado.getTamanho();
            case 5:
                return resultado.getAmostra();
            case 6:
                return resultado.getTempoMillis();
            case 7:
                return resultado.isSucesso() ? "SIM" : "NAO";
            default:
                return "";
        }
    }

    private ResultadoExecucao obterPorIndice(int indice) {
        if (indice < 0 || indice >= registros.size()) {
            return null;
        }

        Iterator<ResultadoExecucao> iterator = registros.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            ResultadoExecucao atual = iterator.next();
            if (i == indice) {
                return atual;
            }
        }
        return null;
    }
}
