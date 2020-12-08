package br.com.luisfga.spring.business;

import br.com.luisfga.spring.business.entities.AppRole;
import br.com.luisfga.spring.business.entities.AppUser;
import br.com.luisfga.spring.business.exceptions.EmailAlreadyTakenException;
import br.com.luisfga.spring.business.exceptions.EmailConfirmationSendingException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {
    
    @Autowired
    public EntityManager em;
    
    @Autowired 
    private MailService mailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public void registerNewAppUser(String email, String password, String userName, LocalDate birthday) 
            throws EmailAlreadyTakenException {
        
        try {
            //verifica se o email informado está disponível
            Query checkIfExists = em.createNamedQuery("AppUser.checkIfExists");
            checkIfExists.setParameter("email", email);
            checkIfExists.getSingleResult();
            
            //se não lançou NoResultException é porque já existe tal email cadastrado
            throw new EmailAlreadyTakenException();
            
        } catch (NoResultException nrException) {
        }
        
        //configura novo usuário
        AppUser newAppUser = new AppUser();
        newAppUser.setEmail(email);
        newAppUser.setUserName(userName);
        newAppUser.setBirthday(birthday);
        newAppUser.setStatus("new");
        newAppUser.setJoinTime(OffsetDateTime.now());

        String encryptedPassword = passwordEncoder.encode(password);
        newAppUser.setPassword(encryptedPassword);
        
        //configura ROLE
        newAppUser.setRoles(getRolesForNewUser());

        //salva novo usuário
        em.persist(newAppUser);

    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String email) throws EmailConfirmationSendingException{
        try {
            mailService.enviarEmailConfirmacaoNovoUsuario(email);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new EmailConfirmationSendingException();
        }
    }
    
    private Set<AppRole> getRolesForNewUser(){
        try {
            Query findRolesForNewUser = em.createNamedQuery("AppRole.findRolesForNewUser");
            //Assumimos que um novo usuário não tem nada e sua lista de Roles está vazia.
            List<AppRole> roles = findRolesForNewUser.getResultList();

            return new HashSet<>(roles);
            
        } catch (NoResultException nrException) {
            //no op
            //todo colocar uma exceção dizendo que o sistema não está configurado, pois é obrigatório que tenha ao menos uma Role.
        }
        return null;
    }
    
}