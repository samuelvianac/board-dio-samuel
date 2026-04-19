package br.com.dio.board.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final Properties PROPERTIES = loadProperties();

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
    }

    private static Properties loadProperties() {
        try (InputStream inputStream = ConnectionFactory.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("Arquivo db.properties não encontrado.");
            }

            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar db.properties.", e);
        }
    }
}
