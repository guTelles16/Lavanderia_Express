/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.service;

import br.com.lavanderiaexpress.dao.ServicoDAO;
import br.com.lavanderiaexpress.model.Servico;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Gusta
 */
public class ServicoService {
    
    private final ServicoDAO dao = new ServicoDAO();
    
    public void cadastrar(String descricao, String precoUnitario) throws SQLException, ParseException {
        // Conversão de "8,50" (String) para BigDecimal
        BigDecimal precoUnitarioDecimal = converterStringParaBigDecimal(precoUnitario);
        
        Servico novo = montar(0, descricao, precoUnitarioDecimal);
        try {
            dao.salvar(novo); 
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Preço unitário não pode ser zero ou negativo.");
        }
    }
    
    public void atualizar(int id, String descricao, String precoUnitario) throws SQLException, ParseException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um serviço.");
        }
        
        // Convertendo String para BigDecimal
        BigDecimal precoUnitarioDecimal = converterStringParaBigDecimal(precoUnitario);
        
        Servico alterado = montar(id, descricao, precoUnitarioDecimal);
        try {
            dao.atualizar(alterado);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Preço unitário não pode ser zero ou negativo.");
        }
    }
    
    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecione um serviço");
        }
        try {
            dao.excluir(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Este serviço possui pedidos e não pode ser excluído.");
        }
    }
    
    public List<Servico> listar(String filtro) throws SQLException {
        return dao.listar(filtro == null ? "" : filtro.trim());
    }
    
    // Método de conversão para BigDecimal com NumberFormat (trata pontos de milhar, tipo "1.250, 50")
    private BigDecimal converterStringParaBigDecimal(String valor) throws ParseException {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe o preço unitário.");
        }
        if (valor.trim().length() > 10) {
            throw new IllegalArgumentException(
                    "O preço unitário deve ter no máximo 8 dígitos inteiros e 2 decimais.");
        }
        
        String valorLimpo = valor.trim();
        
        // Formatador no padrão brasileiro
        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
        
        // ParsePosition serve para rastrear até onde o texto é lido
        ParsePosition posicao = new ParsePosition(0);
        
        Number numero = nf.parse(valorLimpo, posicao);
        if (posicao.getIndex() < valor.trim().length()) {
            throw new IllegalArgumentException("Formato de número inválido para o padrão brasileiro.");
        }
        
        // Se o número for nulo (caso comece com letras)
        if (numero == null) {
            throw new IllegalArgumentException("Não foi possível processar o valor.");
        }
        return new BigDecimal(numero.toString());
    }
    
    private static Servico montar(int id, String descricao, BigDecimal precoUnitario) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Informe a descrição.");
        }
        if (descricao.trim().length() > 100) {
            throw new IllegalArgumentException("A descrição deve ter no máximo 100 caracteres.");
        }
        return new Servico(id, descricao.trim(), precoUnitario);
    }
}
