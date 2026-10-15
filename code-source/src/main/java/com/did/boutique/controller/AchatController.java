package com.did.boutique.controller;

import com.did.boutique.model.Achat;
import com.did.boutique.repository.AchatRepository;
import com.did.boutique.repository.ProduitRepository;
import com.did.boutique.service.AchatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/achats")
public class AchatController {

    private final AchatService achatService;
    private final AchatRepository achatRepository;
    private final ProduitRepository produitRepository;

    @Value("${boutique.nom:Boutique}")
    private String nomBoutique;

    public AchatController(AchatService achatService, AchatRepository achatRepository,
                           ProduitRepository produitRepository) {
        this.achatService = achatService;
        this.achatRepository = achatRepository;
        this.produitRepository = produitRepository;
    }

    @GetMapping
    public String historique(Model model) {
        model.addAttribute("achats", achatRepository.findAllByOrderByDateAchatDesc());
        return "achats/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("produits", produitRepository.findAll(Sort.by("nom")));
        return "achats/form";
    }

    @PostMapping
    public String enregistrer(@RequestParam(required = false) List<Long> produitId,
                              @RequestParam(required = false) List<Integer> quantite,
                              @RequestParam(required = false) String nomClient,
                              @RequestParam(required = false) String telephoneClient,
                              Authentication auth, Model model) {
        try {
            Achat achat = achatService.enregistrerAchat(auth.getName(), nomClient, telephoneClient,
                    produitId, quantite);
            return "redirect:/achats/" + achat.getId() + "/ticket";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            model.addAttribute("produits", produitRepository.findAll(Sort.by("nom")));
            return "achats/form";
        }
    }

    @GetMapping("/{id}/ticket")
    public String ticket(@PathVariable Long id, Model model) {
        return achatRepository.findWithLignesById(id)
                .map(a -> {
                    model.addAttribute("achat", a);
                    model.addAttribute("nomBoutique", nomBoutique);
                    return "achats/ticket";
                })
                .orElse("redirect:/achats");
    }
}