/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.dao;

import br.com.lavanderiaexpress.model.Servico;
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
public class ServicoDAO {
    public void salvar(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (descricao, preco_unitario) VALUES (?, ?)";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.descricao());
            ps.setBigDecimal(2, s.precoUnitario());
            ps.executeUpdate();
        }
    }
    
    public void atualizar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET descricao = ?, preco_unitario = ? WHERE id_servico = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.descricao());
            ps.setBigDecimal(2, s.precoUnitario());
            ps.setInt(3, s.id());
            ps.executeUpdate();
        }
    }
    
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servico WHERE id_servico = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    public List<Servico> listar(String filtro) throws SQLException {
        String sql = "SELECT id_servico, descricao, preco_unitario FROM servico "
                   + "WHERE descricao LIKE ? ORDER BY descricao";
        String padrao = "%" + filtro + "%";
        List<Servico> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, padrao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Servico(
                            rs.getInt("id_servico"),
                            rs.getString("descricao"),
                            rs.getBigDecimal("preco_unitario")));
                }
            }
        }
        return lista;
    }
}
