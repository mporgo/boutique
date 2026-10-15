package com.did.boutique.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute
    public void ajouterContexte(Authentication auth, HttpServletRequest request, Model model) {
        model.addAttribute("uri", request.getRequestURI());
        if (auth != null) {
            model.addAttribute("nomConnecte", auth.getName());
            model.addAttribute("estGerant", auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_GERANT")));
        }
    }
}