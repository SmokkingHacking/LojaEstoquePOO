
package br.edu.unieuro.loja;

import br.edu.unieuro.loja.db.DatabaseInitializer;
import br.edu.unieuro.loja.ui.TelaEntradaEstoque;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // Inicializa o banco de dados H2 local.
        try {
            DatabaseInitializer.initialize();
        } catch (SQLException exception) {
            exception.printStackTrace();

            JOptionPane.showMessageDialog(
                    null,
                    "Não foi possível inicializar o banco H2 local.\n\n"
                            + exception.getMessage(),
                    "Erro ao iniciar o banco",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Utiliza o tema Metal que acompanha o Java Swing.
        // Evita depender do tema visual nativo do sistema operacional.
        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.metal.MetalLookAndFeel"
            );
        } catch (Exception exception) {
            System.err.println(
                    "Não foi possível configurar o tema Swing."
            );
            exception.printStackTrace();
        }

        // Abre a interface gráfica na thread do Swing.
        SwingUtilities.invokeLater(() -> {
            TelaEntradaEstoque tela = new TelaEntradaEstoque();
            tela.setVisible(true);
        });
    }
}
