package br.com.luisfga.spring.web;

import br.com.luisfga.spring.business.PasswordRecoverService;
import br.com.luisfga.spring.business.exceptions.WrongUserDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

@Controller
public class RecoverController {

    @Autowired
    private PasswordRecoverService passwordRecoverService;

    @InitBinder
    public void customizeBinding (WebDataBinder binder) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        binder.registerCustomEditor(Date.class, "birthday", new CustomDateEditor(dateFormatter, true));
    }

    @GetMapping("/passwordRecover")
    public String passwordRevoverInput(Model model){
        model.addAttribute("recoverForm", new RecoverForm());
        return "passwordRecover";
    }

    @PostMapping("/passwordRecover")
    public String passwordRecover(@Valid RecoverForm recoverForm, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response){

        ResourceBundle bundle = ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request));

        if(bindingResult.hasErrors()){
            return "passwordRecover";
        }

        try {
            String windowToken = UUID.randomUUID().toString();
            passwordRecoverService.prepareRecovery(recoverForm.getEmail(), recoverForm.getBirthday(), windowToken);

            passwordRecoverService.enviarEmailResetSenha(recoverForm.getEmail(), windowToken);

            request.setAttribute("successMessage", bundle.getString("success.reset.password.email.sent"));
        } catch (WrongUserDetailsException e) {
            bindingResult.addError(new ObjectError("recoverForm", bundle.getString("validation.error.invalid.info")));
        }

        return "passwordRecover";
    }
}
