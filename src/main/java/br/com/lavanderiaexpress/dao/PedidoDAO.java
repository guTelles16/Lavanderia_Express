/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.dao;

import br.com.lavanderiaexpress.model.ItemPedido;
import br.com.lavanderiaexpress.util.Conexao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
