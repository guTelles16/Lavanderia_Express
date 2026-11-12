/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.dao;

import br.com.lavanderiaexpress.model.ItemPedido;
import br.com.lavanderiaexpress.model.LinhaCaixa;
import br.com.lavanderiaexpress.model.PedidoResumo;
import br.com.lavanderiaexpress.model.StatusPedido;
import br.com.lavanderiaexpress.util.Conexao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class PedidoDAO {
    // Gravar pedido e itens numa única transação. Devolve o id do pedido
    public int salvar(int clienteId, List<ItemPedido> itens, BigDecimal total)
            throws SQLException {
        
        String sqlPedido = "INSERT INTO pedido (cliente_id, valor_total) VALUES (?, ?)";
        String sqlItem = "INSERT INTO item_pedido "
                       + "(pedido_id, servico_id, quantidade, preco_unitario) "
                       + "VALUES (?, ?, ?, ?)";
        
        try (Connection con = Conexao.getConnection()) {
             con.setAutoCommit(false); // abre a transação
             try {
                 int pedidoId;
                 try (PreparedStatement ps = con.prepareStatement(
                         sqlPedido, Statement.RETURN_GENERATED_KEYS)) { // devolve o id que o AUTO_INCREMENT acabou de gerar
                    ps.setInt(1, clienteId);
                    ps.setBigDecimal(2, total);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Não foi possível obter o id do pedido.");
                        }
                        pedidoId = rs.getInt(1);
                    }
                 }
             
                try (PreparedStatement ps = con.prepareStatement(sqlItem)) {
                    for (ItemPedido item : itens) {
                        ps.setInt(1, pedidoId);
                        ps.setInt(2, item.servicoId());
                        ps.setInt(3, item.quantidade());
                        ps.setBigDecimal(4, item.precoUnitario());
                        ps.addBatch(); // envia todos os itens de uma vez
                    }
                    ps.executeBatch();
                }
             
                con.commit(); // tudo certo: confirma
                return pedidoId;
            } catch (SQLException e) {
                con.rollback(); // algo falhou: desfaz tudo
                throw e;
            }
        }
    }
    
    public List<PedidoResumo> listar(String filtro, StatusPedido status) throws SQLException {
        StringBuilder sql = new StringBuilder( // acrescenta um trecho fixo no SQL
                "SELECT p.id_pedido, c.nome, p.data_pedido, p.status_pedido, p.valor_total "
              + "FROM pedido p JOIN cliente c ON c.id_cliente = p.cliente_id "
              + "WHERE (c.nome LIKE ? OR CAST(p.id_pedido AS CHAR) LIKE ?) ");
        if (status != null) {
            sql.append("AND p.status_pedido = ? ");
        }
        sql.append("ORDER BY p.data_pedido DESC, p.id_pedido DESC");
        
        String padrao = "%" + filtro + "%";
        List<PedidoResumo> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, padrao);
            ps.setString(2, padrao);
            if (status != null) {
                ps.setString(3, status.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new PedidoResumo(
                            rs.getInt("id_pedido"),
                            rs.getString("nome"),
                            rs.getTimestamp("data_pedido").toLocalDateTime(),
                            StatusPedido.valueOf(rs.getString("status_pedido")),
                            rs.getBigDecimal("valor_total")));
                }
            }
        }
        return lista;
    }
    
    public List<ItemPedido> listarItens(int pedidoId) throws SQLException {
        String sql = "SELECT s.id_servico, s.descricao, i.quantidade, i.preco_unitario "
                   + "FROM item_pedido i JOIN servico s ON s.id_servico = i.servico_id "
                   + "WHERE i.pedido_id = ? ORDER BY i.id_item";
        List<ItemPedido> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ItemPedido(
                            rs.getInt("id_servico"),
                            rs.getString("descricao"),
                            rs.getInt("quantidade"),
                            rs.getBigDecimal("preco_unitario")));
                }
            }
        }
        return lista;
    }
    
    // Atualização do status do pedido. Só atualiza se o status estiver atual.
    public boolean atualizarStatus(int pedidoId, StatusPedido atual, StatusPedido novo)
            throws SQLException {
        String sql = "UPDATE pedido SET status_pedido = ? "
                   + "WHERE id_pedido = ? AND status_pedido = ?";
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, novo.name());
            ps.setInt(2, pedidoId);
            ps.setString(3, atual.name());
            return ps.executeUpdate() == 1;
        }
    }
    
    // Usa um intervalo (>= hoje 00:00 e < amanhã 00:00). Organiza pela ordem do fluxo (ENUM)
    public List<LinhaCaixa> resumoDoDia(LocalDate dia) throws SQLException {
        String sql = "SELECT status_pedido, COUNT(*) AS qtd, "
                   + "COALESCE(SUM(valor_total), 0) AS total "
                   + "FROM pedido WHERE data_pedido >= ? AND data_pedido < ? "
                   + "GROUP BY status_pedido ORDER BY status_pedido";
        List<LinhaCaixa> lista = new ArrayList<>();
        try (Connection con = Conexao.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(dia.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(dia.plusDays(1).atStartOfDay()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new LinhaCaixa(
                            StatusPedido.valueOf(rs.getString("status_pedido")),
                            rs.getInt("qtd"),
                            rs.getBigDecimal("total")));
                }
            }
        }
        return lista;
    }
}