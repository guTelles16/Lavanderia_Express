/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.dao;

import br.com.lavanderiaexpress.model.Cliente;
import br.com.lavanderiaexpress.util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class ClienteDAO {
    public void salvar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, endereco) VALUES (?, ?, ?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.nome());
            ps.setString(2, c.telefone());
            ps.setString(3, c.endereco());
            ps.executeUpdate();
        }
    }
    
    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, endereco = ? WHERE id_cliente = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.nome());
            ps.setString(2, c.telefone());
            ps.setString(3, c.endereco());
            ps.setInt(4, c.id());
            ps.executeUpdate();
        }
    }
    
    public void excluir (int id)throws SQLException {
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    // "%" colocado no valor do parâmentro, busca protegida contra SQL Injection
    public List<Cliente> listar(String filtro) throws SQLException {
        String sql = "SELECT id_cliente, nome, telefone, endereco FROM cliente "
                   + "WHERE nome LIKE ? OR telefone LIKE ? ORDER BY nome";
        String padrao = "%" + filtro + "%";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, padrao);
            ps.setString(2, padrao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Cliente(
                            rs.getInt("id_cliente"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("endereco")));
                }
            }
        }
        return lista;
    }
}
