/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.service;

import br.com.lavanderiaexpress.dao.ClienteDAO;
import br.com.lavanderiaexpress.model.Cliente;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 *
 * @author Gusta
 */
public class ClienteService {
    
    private final ClienteDAO dao = new ClienteDAO();
    
    public void cadastrar(String nome, String telefone, String endereco) throws SQLException {
        Cliente novo = montar(0, nome, telefone, endereco);
        try {
            dao.salvar(novo);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Já existe um cliente com este telefone.");
        }
    }
    
    public void atualizar(int id, String nome, String telefone, String endereco) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um cliente.");
        }
        Cliente alterado = montar(id, nome, telefone, endereco);
        try {
            dao.atualizar(alterado);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Já existe um cliente com este telefone.");
        }
    }
    
    public void excluir (int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um cliente.");
        }
        try {
            dao.excluir(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Este cliente possui pedidos e não pode ser excluído.");
        }
    }
    
    public List<Cliente> listar(String filtro) throws SQLException {
        return dao.listar(filtro == null ? "" : filtro.trim());
    }
    
    private static Cliente montar(int id, String nome, String telefone, String endereco) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }
        if (nome.trim().length() > 100) {
            throw new IllegalArgumentException("O nome deve ter no máximo 100 caracteres.");
        }
        String telefoneFormatado = formatarTelefone(telefone);
        String enderecoFormatado = (endereco == null || endereco.isBlank()) ? null : endereco.trim();
        if (enderecoFormatado != null && enderecoFormatado.length() > 200) {
            throw new IllegalArgumentException("O endereço deve ter no máximo 200 caracteres.");
        }
        return new Cliente(id, nome.trim(), telefoneFormatado, enderecoFormatado);
    }
    
    // Aceita telefone na forma "11999991111", "11 99999-1111"...Devolve "(11) 99999-1111"
    public static String formatarTelefone(String entrada) {
        String digitos = (entrada == null) ? "" : entrada.replaceAll("\\D", "");
        if (digitos.length() != 10 && digitos.length() != 11) {
            throw new IllegalArgumentException("Telefone inválido. Informe DDD + número (10 ou 11 dígitos).");
        }
        String ddd = digitos.substring(0, 2);
        String numero = digitos.substring(2);
        int corte = numero.length() - 4;
        return "(" + ddd + ")" + numero.substring(0, corte) + "-" + numero.substring(corte);
    }
}