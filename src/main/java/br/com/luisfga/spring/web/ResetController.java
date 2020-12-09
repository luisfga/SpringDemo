package br.com.luisfga.spring.web;

import br.com.luisfga.spring.business.PasswordResetService;
import br.com.luisfga.spring.business.exceptions.CorruptedLinkageException;
import br.com.luisfga.spring.business.exceptions.ForbidenOperationException;
import br.com.luisfga.spring.business.exceptions.TimeHasExpiredException;
import br.com.luisfga.spring.business.exceptions.WrongUserDetailsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ResourceBundle;
import java.util.UUID;

@Controller
public class ResetController {

    private static final Logger logger = LoggerFactory.getLogger(ResetController.class);

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/passwordReset")
    public String passwordResetInput(Model model,
                                     @RequestParam(required = false) String encodedUserEmail,
                                     @RequestParam(required = false) String windowToken,
                                     HttpServletRequest request){

        logger.debug("Bimbada no passwordResetInput");

        ResourceBundle bundle = ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request));

        ResetForm resetForm = new ResetForm();
        model.addAttribute("resetForm", resetForm);

        try {

            String email = passwordResetService.validateOperationWindow(encodedUserEmail, windowToken);
            resetForm.setEmail(email);

        } catch (TimeHasExpiredException | CorruptedLinkageException | ForbidenOperationException e) {
            request.setAttribute("errorMessage", bundle.getString("invalid.temp.window.token"));

        } catch (Exception e) {
            e.printStackTrace();
        }


        return "passwordReset";
    }

    @PostMapping("/passwordReset")
    public String passwordReset(
            @Valid ResetForm resetForm,
            BindingResult bindingResult, HttpServletRequest request){

        ResourceBundle bundle = ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request));

        //simple password confirmation equality validation
        if (!resetForm.getPassword().isEmpty() //first check if they are not empty or null
                && !resetForm.getPasswordConfirmation().isEmpty()
                //them if they are diferent add error message on the field
                && !resetForm.getPassword().equals(resetForm.getPasswordConfirmation())){
            bindingResult.addError(new FieldError("resetForm", "passwordConfirmation", bundle.getString("validation.password.confirmation.not.equal")));
        }

        if(bindingResult.hasErrors()){
            return "passwordReset";
        }

        passwordResetService.resetPassword(resetForm.getEmail(),resetForm.getPassword());

        request.setAttribute("successMessage", bundle.getString("success.reset.password"));

        return "index";
    }
}
