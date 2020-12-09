package br.com.luisfga.spring.config.security;

import br.com.luisfga.spring.business.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthFilterFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthFilterFailureHandler.class);

    public CustomAuthFilterFailureHandler(MailService mailService){
        this.mailService = mailService;
    }

    private MailService mailService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

        if (e instanceof CustomAuthFilter.AuthenticationCompositeException) {
            logger.debug("AuthenticationCompositeException!");

            CustomAuthFilter.AuthenticationCompositeException ace = (CustomAuthFilter.AuthenticationCompositeException) e;

            String responseParameters = null;
            if (ace.emailLoginException != null) {
                responseParameters = setResponseParameter(responseParameters, "emailLoginError=true");
            }
            if (ace.passwordLoginException != null) {
                responseParameters = setResponseParameter(responseParameters, "passwordLoginError=true");
            }
            response.sendRedirect("/login"+responseParameters);

        } else if (e instanceof InternalAuthenticationServiceException) {
            logger.debug("InternalAuthenticationServiceException!");
            if (e.getCause() instanceof PendingEmailConfirmationException){
                logger.debug("PendingEmailConfirmationException!");

                String email = ((PendingEmailConfirmationException)e.getCause()).email;
                mailService.enviarEmailConfirmacaoNovoUsuario(email);
                response.sendRedirect("/login?pendingConfirmation=true");
            } else {
                /*TODO neste caso não sabemos o que pode ter havido.
                   avisar administrador e coletar as informações necessárias*/
                response.sendRedirect("/login?error=true");
            }

        } else if (e instanceof BadCredentialsException) {
            logger.debug("BadCredentialsException!");
            response.sendRedirect("/login?error=true");
        }

    }

    private String setResponseParameter(String responseParameters, String parameter){
        if(responseParameters == null) {
            responseParameters="?"+parameter;
            logger.debug("Setting emailError: "+responseParameters);
        } else {
            responseParameters+="&"+parameter;
            logger.debug("Setting passError: "+responseParameters);
        }
        return responseParameters;
    }

}
