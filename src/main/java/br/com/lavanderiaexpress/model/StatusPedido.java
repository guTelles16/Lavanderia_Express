/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package br.com.lavanderiaexpress.model;

import java.util.Optional;

/**
 *
 * @author Gusta
 */
public enum StatusPedido {
    // Ordem das constantes não pode mudar
    PENDENTE("Pendente"),
    LAVANDO("Lavando"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue");
    
    private final String rotulo;
    
    StatusPedido(String rotulo) {
        this.rotulo = rotulo;
    }
    
    public String rotulo() {
        return rotulo;
    }
    
    // Próximo estado do fluxo; vazio se o pedido já foi entregue
    public Optional<StatusPedido> proximo() {
        StatusPedido[] todos = values();
        return ordinal() < todos.length - 1 // "ordinal()" é a posição do valor (0,1,2,3)
                ? Optional.of(todos[ordinal() + 1])
                : Optional.empty();
    }
}
