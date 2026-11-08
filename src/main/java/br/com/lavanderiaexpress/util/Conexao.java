/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Gusta
 */
public class Conexao {
    private static final Properties CONFIG = carregarConfig();
    
    private Conexao() {
        // Classe utilitária: Não instancia a classe
    }
    
    private static Properties carregarConfig() {
        Properties propriedade = new Properties();
        try (InputStream in = Conexao.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo db.properties não encontrado em scr/main/resources");
            }
            propriedade.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao ler db.properties", e);
        }
        return propriedade;
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            CONFIG.getProperty("db.url"),
            CONFIG.getProperty("db.user"),
            CONFIG.getProperty("db.password"));
    }
}
