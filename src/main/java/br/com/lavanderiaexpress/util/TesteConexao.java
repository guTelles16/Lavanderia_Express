/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Gusta
 */
public class TesteConexao {
    public static void main(String[] args) {
        String sql = "SELECT id_servico, descricao, preco_unitario FROM servico ORDER BY id_servico";
        long inicio = System.nanoTime();
        
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("Conectado ao MySQL com sucesso!");
            while (rs.next()) {
                System.out.printf("%d - %s - R$ %.2f%n",
                        rs.getInt("id_servico"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("preco_unitario"));
            }
            
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            System.out.println("Tempo total: " + ms + " ms");
            
        } catch (SQLException e) {
            System.err.println("Falha na conexão: " + e.getMessage());
        }
    }
}
