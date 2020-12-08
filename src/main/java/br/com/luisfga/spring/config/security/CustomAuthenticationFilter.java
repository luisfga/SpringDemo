package br.com.luisfga.spring.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {

        String email = obtainUsername(request);
        String password = obtainPassword(request);

        logger.debug("attemptAuthentication");

        //just prepare compositeException
        AuthenticationCompositeException compositeException = new AuthenticationCompositeException();

        if (email == null || email.isEmpty()) {
            //add email exception
            compositeException.emailLoginException = new EmptyEmailFieldAuthException();
        }

        if (password == null || password.isEmpty()) {
            //add password exception
            compositeException.passwordLoginException = new EmptyPasswordFieldAuthException();
        }

        if(compositeException.emailLoginException != null || compositeException.passwordLoginException != null) {
            throw compositeException;
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    class AuthenticationCompositeException extends AuthenticationException {
        AuthenticationException emailLoginException = null;
        AuthenticationException passwordLoginException = null;
        public AuthenticationCompositeException() {
            super(null);
        }
    }

    class EmptyEmailFieldAuthException extends AuthenticationException {
        public EmptyEmailFieldAuthException() {
            super(null);
        }
    }

    class EmptyPasswordFieldAuthException extends AuthenticationException {
        public EmptyPasswordFieldAuthException() {
            super(null);
        }
    }
}
