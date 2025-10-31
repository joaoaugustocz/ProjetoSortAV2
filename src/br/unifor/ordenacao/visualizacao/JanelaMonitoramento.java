package br.unifor.ordenacao.visualizacao;

import br.unifor.ordenacao.teste.ResultadoExecucao;
import br.unifor.ordenacao.util.AlgoritmoDisponivel;
import br.unifor.ordenacao.util.TipoDado;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * Janela Swing responsável pela interação e visualização dos resultados em tempo real.
 */
public class JanelaMonitoramento extends JFrame {

    private final PainelGraficoDesempenho painelGrafico;
    private final ModeloTabelaResultados modeloTabela;
    private final JLabel labelStatus;

    private JComboBox<AlgoritmoDisponivel> comboAlgoritmo;
    private JComboBox<TipoDado> comboTipoDado;
    private JSpinner spinnerTamanho;
    private JSpinner spinnerAmostras;
    private JSpinner spinnerThreads;
    private JButton botaoSequencial;
    private JButton botaoParalelo;
    private JButton botaoComparativo;
    private JButton botaoLimpar;

    private ControladorExecucao controlador;

    public JanelaMonitoramento(List<AlgoritmoDisponivel> algoritmos) {
        super("Monitor de Desempenho - Ordenação Paralela");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel painelControle = criarPainelControle(algoritmos);

        painelGrafico = new PainelGraficoDesempenho();
        JScrollPane scrollGrafico = new JScrollPane(painelGrafico);
        scrollGrafico.setBorder(BorderFactory.createTitledBorder("Tempo médio por algoritmo"));
        scrollGrafico.setPreferredSize(new Dimension(820, 360));
        scrollGrafico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollGrafico, BorderLayout.CENTER);

        labelStatus = new JLabel("Configure os parâmetros e execute um teste.", SwingConstants.CENTER);
        labelStatus.setOpaque(true);
        labelStatus.setBackground(new Color(0xECEFF1));
        labelStatus.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel painelSuperior = new JPanel(new BorderLayout(0, 5));
        painelSuperior.add(painelControle, BorderLayout.CENTER);
        painelSuperior.add(labelStatus, BorderLayout.SOUTH);
        add(painelSuperior, BorderLayout.NORTH);

        modeloTabela = new ModeloTabelaResultados();
        JTable tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setPreferredScrollableViewportSize(new Dimension(820, 180));
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Últimos resultados"));
        add(scrollTabela, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel criarPainelControle(List<AlgoritmoDisponivel> algoritmos) {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Configuração do cenário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        comboAlgoritmo = new JComboBox<>(algoritmos.toArray(new AlgoritmoDisponivel[0]));
        comboAlgoritmo.addActionListener(e -> atualizarDisponibilidadeParalelo());

        comboTipoDado = new JComboBox<>(TipoDado.values());

        spinnerTamanho = new JSpinner(new SpinnerNumberModel(10_000, 100, 1_000_000, 1_000));
        spinnerAmostras = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        spinnerThreads = new JSpinner(new SpinnerNumberModel(4, 1, 64, 1));

        botaoSequencial = new JButton("Executar Sequencial");
        botaoParalelo = new JButton("Executar Paralelo");
        botaoComparativo = new JButton("Comparar Seq x Paralelo");
        botaoLimpar = new JButton("Limpar Visualização");

        botaoSequencial.addActionListener(e -> dispararSequencial());
        botaoParalelo.addActionListener(e -> dispararParalelo());
        botaoComparativo.addActionListener(e -> dispararComparativo());
        botaoLimpar.addActionListener(e -> limparVisualizacao());

        int coluna = 0;

        adicionarLinha(painel, gbc, coluna++, "Algoritmo:", comboAlgoritmo);
        adicionarLinha(painel, gbc, coluna++, "Tipo de dados:", comboTipoDado);
        adicionarLinha(painel, gbc, coluna++, "Tamanho do vetor:", spinnerTamanho);
        adicionarLinha(painel, gbc, coluna++, "Amostras:", spinnerAmostras);
        adicionarLinha(painel, gbc, coluna++, "Threads:", spinnerThreads);

        gbc.gridx = coluna;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(botaoSequencial, gbc);

        gbc.gridy = 2;
        painel.add(botaoParalelo, gbc);

        gbc.gridy = 4;
        painel.add(botaoComparativo, gbc);

        gbc.gridy = 6;
        gbc.gridheight = 1;
        painel.add(botaoLimpar, gbc);

        atualizarDisponibilidadeParalelo();
        desabilitarBotoes();

        return painel;
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String rotulo, java.awt.Component componente) {
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        painel.add(new JLabel(rotulo), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, gbc);
    }

    private void dispararSequencial() {
        if (controlador == null) {
            return;
        }
        AlgoritmoDisponivel algoritmo = (AlgoritmoDisponivel) comboAlgoritmo.getSelectedItem();
        if (algoritmo == null || !algoritmo.possuiSequencial()) {
            atualizarStatus("Versão sequencial indisponível para este algoritmo.");
            return;
        }
        controlador.executarSequencial(
                algoritmo,
                (TipoDado) comboTipoDado.getSelectedItem(),
                ((Number) spinnerTamanho.getValue()).intValue(),
                ((Number) spinnerAmostras.getValue()).intValue()
        );
    }

    private void dispararParalelo() {
        if (controlador == null) {
            return;
        }
        AlgoritmoDisponivel algoritmo = (AlgoritmoDisponivel) comboAlgoritmo.getSelectedItem();
        if (algoritmo == null || !algoritmo.possuiParalelo()) {
            atualizarStatus("Versão paralela indisponível para este algoritmo.");
            return;
        }
        controlador.executarParalelo(
                algoritmo,
                (TipoDado) comboTipoDado.getSelectedItem(),
                ((Number) spinnerTamanho.getValue()).intValue(),
                ((Number) spinnerAmostras.getValue()).intValue(),
                ((Number) spinnerThreads.getValue()).intValue()
        );
    }

    private void dispararComparativo() {
        if (controlador == null) {
            return;
        }
        AlgoritmoDisponivel algoritmo = (AlgoritmoDisponivel) comboAlgoritmo.getSelectedItem();
        if (algoritmo == null) {
            return;
        }

        controlador.executarComparativo(
                algoritmo,
                (TipoDado) comboTipoDado.getSelectedItem(),
                ((Number) spinnerTamanho.getValue()).intValue(),
                ((Number) spinnerAmostras.getValue()).intValue(),
                ((Number) spinnerThreads.getValue()).intValue()
        );
    }

    private void atualizarDisponibilidadeParalelo() {
        AlgoritmoDisponivel algoritmo = (AlgoritmoDisponivel) comboAlgoritmo.getSelectedItem();
        boolean possuiParalelo = algoritmo != null && algoritmo.possuiParalelo();
        spinnerThreads.setEnabled(possuiParalelo);
        botaoParalelo.setEnabled(possuiParalelo && controlador != null);
        botaoComparativo.setEnabled(possuiParalelo && controlador != null);
        if (!possuiParalelo) {
            spinnerThreads.setToolTipText("Nenhuma versão paralela disponível para este algoritmo.");
        } else {
            spinnerThreads.setToolTipText(null);
        }
    }

    private void desabilitarBotoes() {
        botaoSequencial.setEnabled(false);
        botaoParalelo.setEnabled(false);
        botaoComparativo.setEnabled(false);
        botaoLimpar.setEnabled(true);
    }

    /**
     * Define o controlador responsável por executar os cenários.
     */
    public void configurarControlador(ControladorExecucao controlador) {
        this.controlador = controlador;
        AlgoritmoDisponivel algoritmo = (AlgoritmoDisponivel) comboAlgoritmo.getSelectedItem();
        boolean sequencialDisponivel = controlador != null && algoritmo != null && algoritmo.possuiSequencial();
        botaoSequencial.setEnabled(sequencialDisponivel);
        botaoLimpar.setEnabled(true);
        atualizarDisponibilidadeParalelo();
    }

    /**
     * Enfileira a atualização da interface no EDT.
     */
    public void registrarResultado(ResultadoExecucao resultado) {
        SwingUtilities.invokeLater(() -> {
            painelGrafico.registrarResultado(resultado);
            modeloTabela.adicionarResultado(resultado);
            labelStatus.setText(descreverResultado(resultado));
        });
    }

    private String descreverResultado(ResultadoExecucao resultado) {
        return String.format("Último teste: %s | %s | %d elementos | %s | amostra %d | tempo %d ms",
                resultado.getAlgoritmo(),
                resultado.getModo().equals("Paralelo")
                        ? "Paralelo (" + resultado.getThreads() + " threads)"
                        : "Sequencial",
                resultado.getTamanho(),
                resultado.getTipoDado().getDescricao(),
                resultado.getAmostra(),
                resultado.getTempoMillis());
    }

    /**
     * Atualiza a mensagem da barra de status.
     */
    public void atualizarStatus(String mensagem) {
        SwingUtilities.invokeLater(() -> labelStatus.setText(mensagem));
    }

    private void limparVisualizacao() {
        painelGrafico.limpar();
        modeloTabela.limpar();
        atualizarStatus("Visualização limpa. Aguardando novos testes...");
        if (controlador != null) {
            controlador.limparHistorico();
        }
    }
}
