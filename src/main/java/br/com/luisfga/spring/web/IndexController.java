package br.com.luisfga.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class IndexController {
    
    @GetMapping(value = {"","/","index","home"})
    public String index(){
        return "index";
    }
    
    @GetMapping("/requestLocale")
    public String requestLocale(
            @RequestParam(name="lang", required=true, defaultValue="en") String lang,
            HttpServletRequest request, 
            HttpServletResponse response){
        
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        localeResolver.setLocale(request, response, StringUtils.parseLocaleString(lang));
        
        return "redirect:" + request.getHeader("Referer");
    }
    
    @GetMapping("login")
    public String login(){
        return "login";
    }
    
    @GetMapping("dashboard")
    public String dashboard(){
        return "dashboard";
    }
}
