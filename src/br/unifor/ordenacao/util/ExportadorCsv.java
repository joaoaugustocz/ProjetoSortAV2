package br.unifor.ordenacao.util;

import br.unifor.ordenacao.teste.ResultadoExecucao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Responsável por materializar os resultados em arquivo CSV.
 */
public class ExportadorCsv {

    private static final String CABECALHO = "Algoritmo;Modo;Threads;TipoDado;Tamanho;Amostra;TempoMillis;Sucesso;Observacao";

    public void salvar(Path destino, List<ResultadoExecucao> resultados) throws IOException {
        if (destino.getParent() != null) {
            Files.createDirectories(destino.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            writer.write(CABECALHO);
            writer.newLine();
            for (ResultadoExecucao resultado : resultados) {
                writer.write(formatar(resultado));
                writer.newLine();
            }
        }
    }

    private String formatar(ResultadoExecucao resultado) {
        String observacao = resultado.getObservacao() == null ? "" : resultado.getObservacao().replace(";", ",");
        return String.join(";",
                resultado.getAlgoritmo(),
                resultado.getModo(),
                String.valueOf(resultado.getThreads()),
                resultado.getTipoDado().getDescricao(),
                String.valueOf(resultado.getTamanho()),
                String.valueOf(resultado.getAmostra()),
                String.valueOf(resultado.getTempoMillis()),
                resultado.isSucesso() ? "SIM" : "NAO",
                observacao
        );
    }
}
