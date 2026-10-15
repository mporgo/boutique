package com.did.boutique.controller;

import com.did.boutique.repository.ProduitRepository;
import com.did.boutique.service.StatsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProduitRepository produitRepository;
    private final StatsService statsService;

    public HomeController(ProduitRepository produitRepository, StatsService statsService) {
        this.produitRepository = produitRepository;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String home(Authentication auth, Model model) {
        boolean gerant = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GERANT"));
        model.addAttribute("stats", statsService.calculer(gerant));
        model.addAttribute("stockBas", produitRepository.findStockBas());
        return "index";
    }
}