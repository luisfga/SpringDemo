/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.luisfga.spring.web;

import java.time.LocalDate;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author luigi
 */
public class StringToLocalDateConverter implements Converter<String,LocalDate>{

    @Override
    public LocalDate convert(String s) {
        return (s==null||s.isEmpty())?null:LocalDate.parse(s);
    }
    
}
