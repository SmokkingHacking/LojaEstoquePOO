package br.edu.unieuro.loja.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Centraliza a configuracao e a abertura de conexoes JDBC com o H2 local. */
public final class Database {
    private static final String CONFIG_FILE = "/db.properties";
    private static final Properties PROPERTIES = carregarConfiguracao();

    private Database() {
        // Classe utilitaria: nao deve ser instanciada.
    }

    private static Properties carregarConfiguracao() {
        Properties properties = new Properties();
        try (InputStream input = Database.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo db.properties nao encontrado em src/main/resources.");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Nao foi possivel ler db.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = requiredProperty("db.url");
        String user = requiredProperty("db.user");
        String password = PROPERTIES.getProperty("db.password", "");

        // Garante que a pasta do banco local exista antes do H2 abrir o arquivo.
        if (url.startsWith("jdbc:h2:file:./data/")) {
            try {
                Files.createDirectories(Path.of("data"));
            } catch (IOException e) {
                throw new SQLException("Nao foi possivel criar a pasta local 'data'.", e);
            }
        }

        return DriverManager.getConnection(url, user, password);
    }

    private static String requiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("A propriedade '" + key + "' nao foi configurada em db.properties.");
        }
        return value.trim();
    }
}
