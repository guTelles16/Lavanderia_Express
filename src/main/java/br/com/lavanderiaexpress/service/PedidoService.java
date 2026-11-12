/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.service;

import br.com.lavanderiaexpress.dao.PedidoDAO;
import br.com.lavanderiaexpress.model.Cliente;
import br.com.lavanderiaexpress.model.ItemPedido;
import br.com.lavanderiaexpress.model.LinhaCaixa;
import br.com.lavanderiaexpress.model.PedidoResumo;
import br.com.lavanderiaexpress.model.StatusPedido;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class PedidoService {
    
    public static final int QUANTIDADE_MAXIMA = 999;
    
    // modo STRICT com "uuuu" recusa datas como 31/02/2026
    public static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    
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
    
    public List<PedidoResumo> listar(String filtro, StatusPedido status) throws SQLException {
        return dao.listar(filtro == null ? "" : filtro.trim(), status);
    }
    
    public List<ItemPedido> itens(int pedidoId) throws SQLException {
        return dao.listarItens(pedidoId);
    }
    
    public void avancarStatus(PedidoResumo pedido) throws SQLException {
        if (pedido == null) {
            throw new IllegalArgumentException("Selecione um pedido.");
        }
        StatusPedido novo = pedido.status().proximo().orElseThrow(
                () -> new IllegalArgumentException("Este pedido já foi entregue."));
        if (!dao.atualizarStatus(pedido.id(), pedido.status(), novo)) {
            throw new IllegalStateException(
                    "O status deste pedido foi alterado por outra pessoa. Atualize a lista.");
        }
    }
    
    public List<LinhaCaixa> fecharCaixa(LocalDate dia) throws SQLException {
        return dao.resumoDoDia(dia);
    }
    
    public static LocalDate lerData(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Informe a data (dd/mm/aaaa).");
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/mm/aaaa.");
        }
    }
    
    public static BigDecimal somarTotal(List<LinhaCaixa> linhas) {
        BigDecimal soma = BigDecimal.ZERO;
        for (LinhaCaixa l : linhas) {
            soma = soma.add(l.total());
        }
        return soma;
    }
    
    public static int somarQuantidade(List<LinhaCaixa> linhas) {
        int soma = 0;
        for (LinhaCaixa l: linhas) {
            soma += l.quantidade();
        }
        return soma;
    }
}