package br.com.luisfga.spring.business.exceptions;

import org.springframework.security.core.AuthenticationException;

public class PendingEmailConfirmationException extends AuthenticationException {

    public PendingEmailConfirmationException(String msg) {
        super(msg);
    }
}
