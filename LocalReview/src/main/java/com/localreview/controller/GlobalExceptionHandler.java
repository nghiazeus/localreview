package com.localreview.controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return new ModelAndView("error");
    }
    
    @ModelAttribute
    public void addAttributes(Model model) {
        if (model.containsAttribute("success")) {
            model.addAttribute("success", model.asMap().get("success"));
        }
    }
}

