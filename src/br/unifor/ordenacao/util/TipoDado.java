package br.unifor.ordenacao.util;

/**
 * Forma de organização inicial dos dados que serão ordenados.
 */
public enum TipoDado {
    ALEATORIO("Aleatório"),
    ORDENADO("Ordenado"),
    REVERSO("Reverso"),
    QUASE_ORDENADO("Quase Ordenado");

    private final String descricao;

    TipoDado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
