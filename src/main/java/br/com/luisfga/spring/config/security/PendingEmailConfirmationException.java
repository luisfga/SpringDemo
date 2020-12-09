package br.com.luisfga.spring.config.security;

import org.springframework.security.core.AuthenticationException;

public class PendingEmailConfirmationException extends AuthenticationException {

    public String email;

    public PendingEmailConfirmationException(String email) {
        super("Usuário pendente de confirmação: " + email);
        this.email = email;
    }
}
