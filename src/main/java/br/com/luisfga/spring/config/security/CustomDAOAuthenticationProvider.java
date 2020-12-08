package br.com.luisfga.spring.config.security;

import br.com.luisfga.spring.business.MailService;
import br.com.luisfga.spring.business.entities.AppRole;
import br.com.luisfga.spring.business.entities.AppUser;
import br.com.luisfga.spring.business.exceptions.PendingEmailConfirmationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

@Component
public class CustomDAOAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomDAOAuthenticationProvider.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private EntityManager em;

    public CustomDAOAuthenticationProvider(){
        logger.debug("NOVO CustomDAOAuthenticationProvider criado");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.debug("authenticate");

        String email = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        logger.debug(email);
        logger.debug(password);

        //just prepare compositeException
        AuthenticationCompositeException compositeException = new AuthenticationCompositeException();
        if (email == null || email.isEmpty()) {
            logger.debug("Email vazio");
            //add email exception
            compositeException.emailLoginException = new EmptyEmailFieldAuthException();
        }
        if (password == null || password.isEmpty()) {
            logger.debug("Password vazio");
            //add password exception
            compositeException.passwordLoginException = new EmptyPasswordFieldAuthException();
        }
        if(compositeException.emailLoginException != null || compositeException.passwordLoginException != null) {
            logger.debug("Lançando CompositeException");
            throw compositeException;
        }

        AppUser appUser = em.find(AppUser.class, email);
        if (appUser == null) {
            logger.debug("Lançando UsernameNotFoundException");
            throw new UsernameNotFoundException("User not found");

        } else if (appUser.getStatus().equals("new")) {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    mailService.enviarEmailConfirmacaoNovoUsuario(email);

                //TODO enviar email em caso de falha, enviar email para o administrador
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });

            logger.debug("Lançando PendingEmailConfirmationException");
            throw new PendingEmailConfirmationException("Pending email confirmation");
        }

        logger.debug("A princípio, tudo ok!");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        authRequest.setDetails(buildUserForAuthentication(appUser, getUserAuthority(appUser.getRoles())));

        return authRequest;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    private List<GrantedAuthority> getUserAuthority(Set<AppRole> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.toString()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(AppUser appUser, List<GrantedAuthority> authorities) {
        return new User(appUser.getEmail(), appUser.getPassword(), authorities);
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
