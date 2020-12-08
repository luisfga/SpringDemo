package br.com.luisfga.spring.business;

import br.com.luisfga.spring.business.entities.AppUser;
import br.com.luisfga.spring.business.exceptions.CorruptedLinkageException;
import java.util.Base64;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmRegistrationService {
    
    @Autowired
    public EntityManager em;

    @Transactional
    public void confirmRegistration(String encodedEmail) throws CorruptedLinkageException {
        
        if (encodedEmail == null || encodedEmail.isEmpty()) {
            throw new CorruptedLinkageException();
        }
        
        try {
            byte[] decodedEmailBytes = Base64.getDecoder().decode(encodedEmail);
            String email = new String(decodedEmailBytes);
            
            Query findByEmail = em.createNamedQuery("AppUser.findByEmail");
            findByEmail.setParameter("email", email);
            AppUser appUser = (AppUser) findByEmail.getSingleResult();
            
            appUser.setStatus("ok");//seta status para OK, i.e. CONFIRMADO
//            em.persist(appUser);
            
        } catch (NoResultException | IllegalArgumentException nrException) {
            throw new CorruptedLinkageException();
            
        }
    }
    
}
