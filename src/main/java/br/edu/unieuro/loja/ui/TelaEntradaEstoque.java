package br.edu.unieuro.loja.ui;

import br.edu.unieuro.loja.model.EntradaEstoque;
import br.edu.unieuro.loja.model.Produto;
import br.edu.unieuro.loja.service.EstoqueService;
import br.edu.unieuro.loja.exception.RegraNegocioException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Interface Swing usada para demonstrar a entrada de produtos. */
public class TelaEntradaEstoque extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(TelaEntradaEstoque.class.getName());

    private final EstoqueService estoqueService = new EstoqueService();
    private final JComboBox<Produto> comboProdutos = new JComboBox<>();
    private final JTextField campoQuantidade = new JTextField(14);
    private final JTextArea campoObservacao = new JTextArea(4, 22);
    private final JLabel labelEstoqueAtual = new JLabel("Estoque atual: -");
    private final JLabel labelStatus = new JLabel("Pronto para registrar uma entrada.");
    private final JButton botaoRegistrar = new JButton("Registrar entrada");
    private final JButton botaoAtualizar = new JButton("Atualizar produtos");

    public TelaEntradaEstoque() {
        super("Loja UNIEURO - Entrada de Produtos no Estoque");
        configurarJanela();
        construirInterface();
        configurarEventos();
        carregarProdutos(null);
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(620, 480));
        setSize(720, 540);
        setLocationRelativeTo(null);
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Entrada de produtos no estoque", SwingConstants.LEFT);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitulo = new JLabel("Registre mercadorias recebidas e atualize o saldo do produto.");
        JPanel cabecalho = new JPanel(new BorderLayout(4, 4));
        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(subtitulo, BorderLayout.SOUTH);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Dados da entrada"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        formulario.add(new JLabel("Produto:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        comboProdutos.setPreferredSize(new Dimension(360, 30));
        formulario.add(comboProdutos, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        formulario.add(new JLabel("Quantidade recebida:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        campoQuantidade.setToolTipText("Digite um numero inteiro maior que zero.");
        formulario.add(campoQuantidade, c);

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        formulario.add(new JLabel("Observacao:"), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        campoObservacao.setLineWrap(true);
        campoObservacao.setWrapStyleWord(true);
        formulario.add(new JScrollPane(campoObservacao), c);

        c.gridx = 1; c.gridy = 3; c.weightx = 1;
        labelEstoqueAtual.setFont(labelEstoqueAtual.getFont().deriveFont(Font.BOLD, 14f));
        formulario.add(labelEstoqueAtual, c);

        raiz.add(formulario, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout(8, 8));
        JPanel botoes = new JPanel();
        botoes.add(botaoAtualizar);
        botoes.add(botaoRegistrar);
        rodape.add(botoes, BorderLayout.NORTH);
        labelStatus.setBorder(BorderFactory.createEmptyBorder(6, 2, 0, 2));
        rodape.add(labelStatus, BorderLayout.SOUTH);
        raiz.add(rodape, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private void configurarEventos() {
        comboProdutos.addActionListener(event -> atualizarEstoqueExibido());
        botaoAtualizar.addActionListener(event -> {
            Produto selecionado = (Produto) comboProdutos.getSelectedItem();
            carregarProdutos(selecionado == null ? null : selecionado.getId());
        });
        botaoRegistrar.addActionListener(event -> registrarEntrada());
        campoQuantidade.addActionListener(event -> registrarEntrada());
    }

    private void carregarProdutos(Long produtoIdParaSelecionar) {
        try {
            List<Produto> produtos = estoqueService.listarProdutos();
            comboProdutos.setModel(new DefaultComboBoxModel<>(produtos.toArray(new Produto[0])));

            if (produtoIdParaSelecionar != null) {
                for (int i = 0; i < comboProdutos.getItemCount(); i++) {
                    Produto produto = comboProdutos.getItemAt(i);
                    if (produto.getId() == produtoIdParaSelecionar) {
                        comboProdutos.setSelectedIndex(i);
                        break;
                    }
                }
            }

            boolean temProdutos = !produtos.isEmpty();
            comboProdutos.setEnabled(temProdutos);
            botaoRegistrar.setEnabled(temProdutos);
            labelStatus.setText(temProdutos
                    ? "Produtos carregados. Selecione um produto para continuar."
                    : "Nenhum produto cadastrado. O banco local sera preparado na proxima inicializacao.");
            atualizarEstoqueExibido();
        } catch (SQLException | IllegalStateException exception) {
            LOGGER.log(Level.SEVERE, "Falha ao carregar produtos", exception);
            comboProdutos.setModel(new DefaultComboBoxModel<>());
            comboProdutos.setEnabled(false);
            botaoRegistrar.setEnabled(false);
            labelEstoqueAtual.setText("Estoque atual: indisponivel");
            labelStatus.setText("Nao foi possivel conectar ao banco de dados.");
            JOptionPane.showMessageDialog(this,
                    "Nao foi possivel carregar os produtos. Confira a dependencia H2 e o arquivo " +
                            "src/main/resources/db.properties.\n\nDetalhes no console do IntelliJ.",
                    "Erro de conexao", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarEstoqueExibido() {
        Produto produto = (Produto) comboProdutos.getSelectedItem();
        if (produto == null) {
            labelEstoqueAtual.setText("Estoque atual: -");
        } else {
            labelEstoqueAtual.setText("Estoque atual: " + produto.getQuantidadeEstoque() + " unidade(s)");
        }
    }

    private void registrarEntrada() {
        Produto produto = (Produto) comboProdutos.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Cadastre ou selecione um produto antes de continuar.",
                    "Produto obrigatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final int quantidade;
        try {
            String textoQuantidade = campoQuantidade.getText().trim();
            if (textoQuantidade.isEmpty()) {
                throw new NumberFormatException("Campo vazio");
            }
            quantidade = Integer.parseInt(textoQuantidade);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this,
                    "Informe a quantidade usando um numero inteiro, por exemplo: 8.",
                    "Quantidade invalida", JOptionPane.WARNING_MESSAGE);
            campoQuantidade.requestFocusInWindow();
            return;
        }

        botaoRegistrar.setEnabled(false);
        try {
            EntradaEstoque entrada = estoqueService.registrarEntrada(
                    produto.getId(), quantidade, campoObservacao.getText());

            JOptionPane.showMessageDialog(this,
                    "Entrada registrada com sucesso!\n\nProduto: " + produto.getNome() +
                            "\nQuantidade recebida: " + entrada.getQuantidade() +
                            "\nNovo saldo: " + (produto.getQuantidadeEstoque() + entrada.getQuantidade()) +
                            "\nRegistro de entrada: #" + entrada.getId(),
                    "Operacao concluida", JOptionPane.INFORMATION_MESSAGE);

            campoQuantidade.setText("");
            campoObservacao.setText("");
            labelStatus.setText("Entrada registrada e salva no banco de dados.");
            carregarProdutos(produto.getId());
        } catch (RegraNegocioException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Regra de negocio", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar entrada no banco", exception);
            JOptionPane.showMessageDialog(this,
                    "Nao foi possivel salvar a entrada. A operacao foi cancelada. " +
                            "Confira a conexao com o banco.\n\nDetalhes no console do IntelliJ.",
                    "Erro ao salvar", JOptionPane.ERROR_MESSAGE);
        } finally {
            botaoRegistrar.setEnabled(comboProdutos.getItemCount() > 0);
        }
    }
}
