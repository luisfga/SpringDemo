package br.com.luisfga.spring.web;

import br.com.luisfga.spring.business.RegisterUseCase;
import br.com.luisfga.spring.business.exceptions.EmailAlreadyTakenException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class RegisterController implements WebMvcConfigurer{
    
    @Inject
    RegisterUseCase registerUseCase;
    
    @InitBinder("user")
    public void customizeBinding (WebDataBinder binder) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        binder.registerCustomEditor(Date.class, "dateOfBirth",
                                    new CustomDateEditor(dateFormatter, true));
    }
    
    @GetMapping("/register")
    public String registerInput(Model model){
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }
    
    @PostMapping("/register")
    public String register(@Valid RegisterForm registerForm, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response){

        ResourceBundle bundle = ResourceBundle.getBundle("messages", RequestContextUtils.getLocale(request));
        
        //simple password confirmation equality validation
        if (!registerForm.getPassword().isEmpty() //first check if they are not empty or null
                && !registerForm.getPasswordConfirmation().isEmpty()
                //them if they are diferent add error message on the field
                && !registerForm.getPassword().equals(registerForm.getPasswordConfirmation())){
            bindingResult.addError(new FieldError("registerForm", "passwordConfirmation", bundle.getString("validation.password.confirmation.not.equal")));
        }
        
        if(bindingResult.hasErrors()){
            return "register";
        }
        
        try {
            registerUseCase.registerNewAppUser(
                registerForm.getEmail(),
                registerForm.getPassword(),
                registerForm.getUserName(),
                registerForm.getBirthday()
            );
        
            request.setAttribute("successMessage", bundle.getString("success.user.registered"));
            
        } catch (EmailAlreadyTakenException emailAlreadyTakenException){
            bindingResult.addError(new ObjectError("registerForm", bundle.getString("validation.error.email.already.taken")));
        }
        
        return "register";
    }
    
    

}
