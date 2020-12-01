package br.com.luisfga.spring.business.exceptions;

import org.apache.logging.log4j.LogManager;


public class ForbidenOperationException extends Exception {
    public ForbidenOperationException(String email){
        LogManager.getLogger().always().log("Tentativa suspeita de resetar senha do usu\u00e1rio '{'{0}'}'", email);
    }
}
