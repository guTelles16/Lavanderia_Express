/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package br.com.lavanderiaexpress.model;

import java.math.BigDecimal;

/**
 *
 * @author Gusta
 */
public record LinhaCaixa(StatusPedido status, int quantidade, BigDecimal total) { }