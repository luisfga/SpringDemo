package br.com.luisfga.spring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @GetMapping(value = {"","/","index","home"})
    public String index(){
        return "index";
    }

    @Autowired
    private LocaleResolver localeResolver;

    @GetMapping("/requestLocale")
    public String requestLocale(
            @RequestParam(name="lang", required=true, defaultValue="en") String lang,
            HttpServletRequest request, 
            HttpServletResponse response){

        logger.debug("requestLocale("+lang+")");
        Locale locale = StringUtils.parseLocaleString(lang);
        localeResolver.setLocale(request, response, locale);

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