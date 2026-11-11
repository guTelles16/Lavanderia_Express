/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.model;

import java.math.BigDecimal;

/**
 *
 * @author Gusta
 */
public record ItemPedido(int servicoId, String descricao,
                         int quantidade, BigDecimal precoUnitario) {
    // Subtotal é calculado a partir dos próprios dados, em vez de guardado
    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
