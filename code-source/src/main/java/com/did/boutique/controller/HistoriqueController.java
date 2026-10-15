package com.did.boutique.controller;

import com.did.boutique.repository.AchatRepository;
import com.did.boutique.repository.LivraisonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoriqueController {

    private final AchatRepository achatRepository;
    private final LivraisonRepository livraisonRepository;

    public HistoriqueController(AchatRepository achatRepository, LivraisonRepository livraisonRepository) {
        this.achatRepository = achatRepository;
        this.livraisonRepository = livraisonRepository;
    }

    @GetMapping("/historique")
    public String historique(Model model) {
        model.addAttribute("achats", achatRepository.findAllByOrderByDateAchatDesc());
        model.addAttribute("livraisons", livraisonRepository.findAllByOrderByDateLivraisonDesc());
        return "historique";
    }
}