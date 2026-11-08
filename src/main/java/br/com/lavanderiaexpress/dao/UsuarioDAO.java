/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.dao;

import br.com.lavanderiaexpress.model.Usuario;
import br.com.lavanderiaexpress.util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author Gusta
 */
public class UsuarioDAO {
    
    public void salvar(Usuario u) throws SQLException {
        // "?" impede SQL Injection (colocar o e-mail direto no SQL dá margem para escrever código SQL)
        String sql = "INSERT INTO usuario (nome, email, senha_hash) VALUES (?, ?, ?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.nome());
            ps.setString(2, u.email());
            ps.setString(3, u.senhaHash());
            ps.executeUpdate();
        }
    }
    
    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id_usuario, nome, email, senha_hash FROM usuario "
                   + "WHERE email = ? AND ativo = TRUE";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                           rs.getInt("id_usuario"),
                           rs.getString("nome"),
                           rs.getString("email"),
                           rs.getString("senha_hash")));
                }
                return Optional.empty();
            }
        }
    }
}