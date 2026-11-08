/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.model;

/**
 *
 * @author Gusta
 */
// "record" cria construtores e leitores automaticamente
public record Usuario(int id, String nome, String email, String senhaHash) {}
