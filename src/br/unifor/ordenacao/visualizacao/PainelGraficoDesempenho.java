package br.unifor.ordenacao.visualizacao;

import br.unifor.ordenacao.teste.ResultadoExecucao;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Painel customizado para exibir um gráfico de barras animado com as
 * médias de tempo dos algoritmos testados.
 */
public class PainelGraficoDesempenho extends JPanel {

    private static final int ALTURA_EIXO = 40;
    private static final int LARGURA_MINIMA_BARRA = 50;
    private static final int ESPACO_BARRA = 20;
    private static final int LARGURA_BASE = 820;

    private final Map<String, EstatisticaCrescente> estatisticas = new LinkedHashMap<>();
    private final Map<String, Double> valoresExibidos = new LinkedHashMap<>();
    private final Timer animador;

    private static final int ALTURA_MINIMA = 360;

    public PainelGraficoDesempenho() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(LARGURA_BASE, ALTURA_MINIMA));

        animador = new Timer(30, e -> atualizarAnimacao());
        animador.setRepeats(true);
    }

    /**
     * Atualiza o gráfico com o resultado informado.
     *
     * @param resultado execução recém finalizada.
     */
    public void registrarResultado(ResultadoExecucao resultado) {
        String chave = resultado.chaveAgrupamento();
        EstatisticaCrescente estatistica = estatisticas.computeIfAbsent(chave, k -> new EstatisticaCrescente());
        estatistica.adicionar(resultado.getTempoMillis());
        valoresExibidos.putIfAbsent(chave, 0.0);
        atualizarDimensaoPreferida();
        if (!animador.isRunning()) {
            animador.start();
        }
        repaint();
    }

    private void atualizarAnimacao() {
        boolean precisaContinuar = false;
        for (Map.Entry<String, EstatisticaCrescente> entry : estatisticas.entrySet()) {
            String chave = entry.getKey();
            double alvo = entry.getValue().media();
            double atual = valoresExibidos.getOrDefault(chave, 0.0);
            double proximo = aproximar(atual, alvo);
            valoresExibidos.put(chave, proximo);
            if (Math.abs(proximo - alvo) > 0.5) {
                precisaContinuar = true;
            }
        }

        if (!precisaContinuar) {
            animador.stop();
        }

        repaint();
    }

    private double aproximar(double atual, double alvo) {
        double diferenca = alvo - atual;
        return atual + diferenca * 0.15;
    }

    /**
     * Remove dados exibidos e restaura dimensões iniciais.
     */
    public void limpar() {
        animador.stop();
        estatisticas.clear();
        valoresExibidos.clear();
        setPreferredSize(new Dimension(LARGURA_BASE, ALTURA_MINIMA));
        revalidate();
        repaint();
    }

    private void atualizarDimensaoPreferida() {
        int quantidade = Math.max(valoresExibidos.size(), 1);
        int larguraCalculada = 120 + quantidade * (LARGURA_MINIMA_BARRA + ESPACO_BARRA);
        int largura = Math.max(LARGURA_BASE, larguraCalculada);
        Dimension atual = getPreferredSize();
        if (atual.width != largura) {
            setPreferredSize(new Dimension(largura, ALTURA_MINIMA));
            revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int larguraDisponivel = getWidth();
        int baseEixoY = getHeight() - ALTURA_EIXO;

        // Desenha eixo
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(40, baseEixoY, larguraDisponivel - 20, baseEixoY);

        if (valoresExibidos.isEmpty()) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(getFont().deriveFont(Font.BOLD, 16f));
            g2d.drawString("Os resultados aparecerão aqui durante as execuções...", 60, baseEixoY - 20);
            g2d.dispose();
            return;
        }

        double maiorValor = valoresExibidos.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1d);

        int quantidade = valoresExibidos.size();
        int espacoTotalBarras = larguraDisponivel - 60;
        int larguraBarra = Math.max(LARGURA_MINIMA_BARRA,
                (espacoTotalBarras - (quantidade - 1) * ESPACO_BARRA) / Math.max(1, quantidade));

        int x = 50;
        int corIndex = 0;
        for (Map.Entry<String, Double> entrada : valoresExibidos.entrySet()) {
            String chave = entrada.getKey();
            double valor = entrada.getValue();

            int alturaBarra = (int) ((valor / maiorValor) * (getHeight() - ALTURA_EIXO - 60));
            int y = baseEixoY - alturaBarra;

            g2d.setColor(gerarCor(corIndex++));
            g2d.fillRoundRect(x, y, larguraBarra, Math.max(5, alturaBarra), 12, 12);

            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(getFont().deriveFont(Font.BOLD, 12f));
            String rotuloTempo = String.format("%.1f ms", valor);
            g2d.drawString(rotuloTempo, x + 4, y - 8);

            g2d.setFont(getFont().deriveFont(Font.PLAIN, 11f));
            desenharTextoMultiLinha(g2d, chave, x, baseEixoY + 15, larguraBarra);

            x += larguraBarra + ESPACO_BARRA;
        }

        g2d.dispose();
    }

    private void desenharTextoMultiLinha(Graphics2D g2d, String texto, int x, int y, int larguraMaxima) {
        String[] partes = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();
        int alturaLinha = g2d.getFontMetrics().getHeight();
        int yAtual = y;

        for (String parte : partes) {
            String tentativa = linhaAtual.length() == 0 ? parte : linhaAtual + " " + parte;
            if (g2d.getFontMetrics().stringWidth(tentativa) > larguraMaxima) {
                g2d.drawString(linhaAtual.toString(), x, yAtual);
                yAtual += alturaLinha;
                linhaAtual = new StringBuilder(parte);
            } else {
                linhaAtual = new StringBuilder(tentativa);
            }
        }

        if (linhaAtual.length() > 0) {
            g2d.drawString(linhaAtual.toString(), x, yAtual);
        }
    }

    private Color gerarCor(int indice) {
        Color[] paleta = {
                new Color(0x4CAF50),
                new Color(0x2196F3),
                new Color(0xFF9800),
                new Color(0x9C27B0),
                new Color(0xF44336),
                new Color(0x009688),
                new Color(0x3F51B5),
                new Color(0x795548)
        };
        return paleta[indice % paleta.length];
    }

    private static class EstatisticaCrescente {
        private long soma;
        private long quantidade;

        void adicionar(long valor) {
            soma += valor;
            quantidade++;
        }

        double media() {
            if (quantidade == 0) {
                return 0;
            }
            return (double) soma / quantidade;
        }
    }
}
