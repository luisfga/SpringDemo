package br.com.luisfga.spring.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.Executors;

@Service
public class MailService {

    @Autowired
    private EntityManager em;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ServletContext servletContext;

    @Value("${spring.mail.username}")
    private String fromStringValue;

    @Value("${server.base.link}")
    private String serverBaseLink;

    @Value("${password.reset.action}")
    private String passwordResetAction;

    @Value("${register.confirmation.action}")
    private String registerConfirmationAction;

    private String getUserName(String email){
        Query findUserNameByEmail = em.createNamedQuery("AppUser.findUserNameByEmail");
        findUserNameByEmail.setParameter("email", email);
        return (String) findUserNameByEmail.getSingleResult();
    }

    public void enviarEmailResetSenha(String destEmail, String windowToken) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                enviarEmailReset(destEmail,windowToken);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    private void enviarEmailReset(String destEmail, String windowToken) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromStringValue, servletContext.getContextPath().replace("/", "")); // Remetente
        helper.setTo(destEmail);
        helper.setSubject("Redefinição de Senha");// Assunto

        //corpo da mensagem
        String style = "<style>"
                        + ".button{"
                            + "background-color: #0099ff; color: white; padding: 5px 10px 5px 10px; "
                            + "vertical-align: middle; text-align: center; text-decoration: none; border-radius: 20px; "
                            + "font-size: 15px;"
                        + "}"
                    + "</style>";

        String msg = style
                + "<h2>Olá, "+getUserName(destEmail)+".</h2>"
                + "<h4>Utilize o botão abaixo para acessar a página de redefinição de senha</h4>"
                + "<a class=\"button\" href=\""+serverBaseLink+servletContext.getContextPath()+passwordResetAction
                + "?encodedUserEmail="+Base64.getEncoder().encodeToString(destEmail.getBytes("UTF-8"))
                + "&windowToken="+windowToken+"\">Redefinir Senha</a><br/><br/>"
                + "*Se não foi você que solicitou a redefinição de senha. Desconsidere essa mensagem.<br/><br/>"
                + "*Este link só funcionará uma única vez. Se necessário, solicite novamente.<br/><br/>";

        helper.setText(msg,true);

        emailSender.send(message);
    }

    public void enviarEmailConfirmacaoNovoUsuario(String destEmail) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                enviarEmailConfirmacao(destEmail);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    private void enviarEmailConfirmacao(String destEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromStringValue,servletContext.getContextPath().replace("/", "")); // Remetente
        helper.setTo(destEmail);
        message.setSubject("Confirmação de Nova Conta");// Assunto

        //corpo da mensagem
        String msg = ""
                + "<style>"
                + ".button{background-color: #0099ff; color: white; padding: 5px 10px 5px 10px; "
                + "vertical-align: middle; text-align: center; text-decoration: none; border-radius: 20px; "
                + "font-size: 15px;}"
                + "</style>"
                + "<h2>Olá, " + getUserName(destEmail) + ".</h2>"
                + "<h4>Utilize o botão abaixo para confirmar sua conta recém criada</h4>"
                + "<a class=\"button\" href=\""
                + serverBaseLink + servletContext.getContextPath() + registerConfirmationAction
                + "?encodedUserEmail=" + Base64.getEncoder().encodeToString(destEmail.getBytes("UTF-8"))  + "\">Confirmar</a><br/><br/>";

        helper.setText(msg,true);

        emailSender.send(message);
    }
}