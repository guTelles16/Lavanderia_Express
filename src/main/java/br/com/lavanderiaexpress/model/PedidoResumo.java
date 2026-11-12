/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.com.lavanderiaexpress.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Gusta
 */
public record PedidoResumo(int id, String clienteNome, LocalDateTime data,
                            StatusPedido status, BigDecimal valorTotal) { }