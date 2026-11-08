/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.lavanderiaexpress.service;

import br.com.lavanderiaexpress.dao.UsuarioDAO;
import br.com.lavanderiaexpress.model.Usuario;
import br.com.lavanderiaexpress.util.Senha;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 *
 * @author Gusta
 */
public class UsuarioService {
    
    public static final int SENHA_MINIMA = 6;
    private static final Pattern EMAIL =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    
    private final UsuarioDAO dao = new UsuarioDAO();
    
    public void cadastrar(String nome, String email, char[] senha) throws SQLException {
        validar(nome, email, senha);
        Usuario novo = new Usuario(0, nome.trim(),
                email.trim().toLowerCase(), Senha.gerarHash(senha));
        try {
            dao.salvar(novo);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }
    }
    
    public Optional<Usuario> autenticar(String email, char[] senha) throws SQLException {
        if (email == null || email.isBlank() || senha == null || senha.length == 0) {
            return Optional.empty();
        }
        return dao.buscarPorEmail(email.trim().toLowerCase())
                .filter(u -> Senha.conferir(senha, u.senhaHash()));
    }
    
    public static void validar(String nome, String email, char[] senha) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome.");
        }
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (senha == null || senha.length < SENHA_MINIMA) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos " + SENHA_MINIMA + " caracteres.");
        }
    }
}