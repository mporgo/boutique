package com.did.boutique.controller;

import com.did.boutique.repository.GrossisteRepository;
import com.did.boutique.repository.ProduitRepository;
import com.did.boutique.service.LivraisonService;
import com.did.boutique.repository.LivraisonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/livraisons")
public class LivraisonController {

    private final LivraisonService livraisonService;
    private final ProduitRepository produitRepository;
    private final GrossisteRepository grossisteRepository;
    private final LivraisonRepository livraisonRepository;

    @Value("${boutique.nom:Boutique}")
    private String nomBoutique;

    public LivraisonController(LivraisonService livraisonService, ProduitRepository produitRepository,
                               GrossisteRepository grossisteRepository, LivraisonRepository livraisonRepository) {
        this.livraisonService = livraisonService;
        this.produitRepository = produitRepository;
        this.grossisteRepository = grossisteRepository;
        this.livraisonRepository = livraisonRepository;
    }
    
    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        remplirListes(model);
        return "livraisons/form";
    }

    @PostMapping
    public String enregistrer(@RequestParam(required = false) Long grossisteId,
                              @RequestParam(required = false) String nomGrossiste,
                              @RequestParam(required = false) String telephoneGrossiste,
                              @RequestParam(required = false) List<Long> produitId,
                              @RequestParam(required = false) List<Integer> quantite,
                              @RequestParam(required = false) List<BigDecimal> prixAchat,
                              Authentication auth, Model model, RedirectAttributes ra) {
        try {
            livraisonService.enregistrerLivraison(auth.getName(), grossisteId, nomGrossiste,
                    telephoneGrossiste, produitId, quantite, prixAchat);
            ra.addFlashAttribute("succes", "Livraison enregistrée : le stock a été mis à jour.");
            return "redirect:/historique";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            remplirListes(model);
            return "livraisons/form";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return livraisonRepository.findWithLignesById(id)
                .map(l -> {
                    model.addAttribute("livraison", l);
                    return "livraisons/details";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erreur", "Livraison introuvable.");
                    return "redirect:/historique";
                });
    }

    @GetMapping("/{id}/bon")
    public String bon(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return livraisonRepository.findWithLignesById(id)
                .map(l -> {
                    model.addAttribute("livraison", l);
                    model.addAttribute("nomBoutique", nomBoutique);
                    return "livraisons/bon";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erreur", "Livraison introuvable.");
                    return "redirect:/historique";
                });
    }

    private void remplirListes(Model model) {
        model.addAttribute("produits", produitRepository.findAll(Sort.by("nom")));
        model.addAttribute("grossistes", grossisteRepository.findAll(Sort.by("nom")));
    }
}