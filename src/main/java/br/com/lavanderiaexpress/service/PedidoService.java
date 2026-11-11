/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.service;

import br.com.lavanderiaexpress.dao.PedidoDAO;
import br.com.lavanderiaexpress.model.Cliente;
import br.com.lavanderiaexpress.model.ItemPedido;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class PedidoService {
    
    public static final int QUANTIDADE_MAXIMA = 999;
    
    private final PedidoDAO dao = new PedidoDAO();
    
    public int registrar (Cliente cliente, List<ItemPedido> itens) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("Selecione um cliente.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item ao pedido.");
        }
        return dao.salvar(cliente.id(), itens, calcularTotal(itens));
    }
    
    public static BigDecimal calcularTotal(List<ItemPedido> itens) {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.subtotal());
        }
        return total;
    }
    
    public static int lerQuantidade(String texto) {
        try {
            int quantidade = Integer.parseInt(texto == null ? "" : texto.trim());
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
            }
            if (quantidade > QUANTIDADE_MAXIMA) {
                throw new IllegalArgumentException (
                        "A quantidade deve ser no máximo " + QUANTIDADE_MAXIMA + ".");
            }
            return quantidade;
        } catch (NumberFormatException e) { // captura erros de conversão
            throw new IllegalArgumentException("Informe uma quantidade válida (número inteiro).");
        }
    }
}