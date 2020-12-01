package br.com.luisfga.spring.business;

import br.com.luisfga.spring.business.exceptions.EmailConfirmationSendingException;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //TODO atenção para os testes. Ver se pode ser Stateless mesmo ou se tem que ser Statefull
public class LoginUseCase {
    
    @Autowired
    public EntityManager em;
    
    @Autowired 
    private MailHelper mailHelper;
    
    public void login(String email, String password) /*throws LoginException, PendingEmailConfirmationException*/ {

//        UsernamePasswordToken authToken = new UsernamePasswordToken(email, password);
//        authToken.setRememberMe(false);
//        
//        Subject currentUser = SecurityUtils.getSubject();
//        
//        try {
//            currentUser.login(authToken);
//            
//        } catch ( UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ice ) {
//            throw new LoginException();
//            
//        } catch (PendingEmailConfirmationShiroAuthenticationException ex){
//            throw new PendingEmailConfirmationException();
//        }

    }
    
    public void logout() {

//        SecurityUtils.getSubject().logout();
        
    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String contextPath, String email) throws EmailConfirmationSendingException{
//        try {
//            mailHelper.enviarEmailConfirmacaoNovoUsuario(contextPath, email);
//            
//        } catch (MessagingException | UnsupportedEncodingException ex) {
//            throw new EmailConfirmationSendingException();
//        }
    }

}
