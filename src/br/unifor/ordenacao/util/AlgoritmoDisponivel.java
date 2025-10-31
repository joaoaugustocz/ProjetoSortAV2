package br.unifor.ordenacao.util;

import br.unifor.ordenacao.algoritmos.ParallelSortAlgorithm;
import br.unifor.ordenacao.algoritmos.SortAlgorithm;

/**
 * Agrupa as implementações sequencial e paralela de um mesmo algoritmo base.
 */
public class AlgoritmoDisponivel {

    private final String nomeBase;
    private final SortAlgorithm sequencial;
    private final ParallelSortAlgorithm paralelo;

    public AlgoritmoDisponivel(String nomeBase,
                               SortAlgorithm sequencial,
                               ParallelSortAlgorithm paralelo) {
        this.nomeBase = nomeBase;
        this.sequencial = sequencial;
        this.paralelo = paralelo;
    }

    public String getNomeBase() {
        return nomeBase;
    }

    public SortAlgorithm getSequencial() {
        return sequencial;
    }

    public ParallelSortAlgorithm getParalelo() {
        return paralelo;
    }

    public boolean possuiSequencial() {
        return sequencial != null;
    }

    public boolean possuiParalelo() {
        return paralelo != null;
    }

    @Override
    public String toString() {
        return nomeBase;
    }
}
