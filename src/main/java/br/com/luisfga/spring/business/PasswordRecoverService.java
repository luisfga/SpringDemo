package br.com.luisfga.spring.business;

import br.com.luisfga.spring.business.entities.AppUser;
import br.com.luisfga.spring.business.entities.AppUserOperationWindow;
import br.com.luisfga.spring.business.exceptions.EmailConfirmationSendingException;
import br.com.luisfga.spring.business.exceptions.WrongUserDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class PasswordRecoverService {
    
    @Autowired
    public EntityManager em;
    
    @Autowired 
    private MailService mailService;

    public void prepareRecovery(String email, LocalDate birthday, String token) 
            throws WrongUserDetailsException {
        
        try {
            Query findByEmail = em.createNamedQuery("AppUser.findByEmail");
            findByEmail.setParameter("email", email);

            AppUser appUser = (AppUser) findByEmail.getSingleResult();
            
            if (!appUser.getBirthday().equals(birthday)) {
                throw new WrongUserDetailsException();
            }

            //reaproveitamento de janela de operação. Caso já exista uma, apenas atualiza a existente ao invés de excluir e criar uma nova.
            AppUserOperationWindow operationWindow = em.find(AppUserOperationWindow.class, appUser.getEmail());
            appUser.setOperationWindow(operationWindow);
            if (appUser.getOperationWindow() == null) {
                operationWindow = new AppUserOperationWindow();
                operationWindow.setAppUser(appUser);
            }
            
            operationWindow.setWindowToken(token);
            operationWindow.setInitTime(OffsetDateTime.now());
            em.persist(operationWindow);
            
        } catch (NoResultException nrException) {
            throw new WrongUserDetailsException();
            
        }
    }
    
    public void enviarEmailResetSenha(String contextName, String email, String windowToken) 
            throws EmailConfirmationSendingException{
        
            mailService.enviarEmailResetSenha(email, windowToken);

    }
    
}