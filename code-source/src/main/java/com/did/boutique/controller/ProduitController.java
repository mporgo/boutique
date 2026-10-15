package com.did.boutique.controller;

import com.did.boutique.model.Produit;
import com.did.boutique.repository.CategorieRepository;
import com.did.boutique.repository.ProduitRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.math.BigDecimal;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    public ProduitController(ProduitRepository produitRepository, CategorieRepository categorieRepository) {
        this.produitRepository = produitRepository;
        this.categorieRepository = categorieRepository;
    }

    // Liste + recherche par nom
    @GetMapping
    public String liste(@RequestParam(required = false) String q, Model model) {
        List<Produit> produits = (q == null || q.isBlank())
                ? produitRepository.findAll(Sort.by("nom"))
                : produitRepository.findByNomContainingIgnoreCase(q.trim());
        model.addAttribute("produits", produits);
        model.addAttribute("q", q);
        return "produits/liste";
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("categories", categorieRepository.findAll(Sort.by("nom")));
        return "produits/form";
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return produitRepository.findById(id)
                .map(p -> {
                    model.addAttribute("produit", p);
                    model.addAttribute("categories", categorieRepository.findAll(Sort.by("nom")));
                    return "produits/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erreur", "Produit introuvable.");
                    return "redirect:/produits";
                });
    }

    @PostMapping
    public String enregistrer(@Valid @ModelAttribute("produit") Produit produit,
                              BindingResult result,
                              @RequestParam(required = false) Long categorieId,
                              Model model,
                              RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categorieRepository.findAll(Sort.by("nom")));
            return "produits/form";
        }
        if (produit.getPrixAchatMoyen() == null) {
            produit.setPrixAchatMoyen(BigDecimal.ZERO);
        }
        produit.setCategorie(categorieId == null ? null
                : categorieRepository.findById(categorieId).orElse(null));
        produitRepository.save(produit);
        ra.addFlashAttribute("succes", "Produit enregistré.");
        return "redirect:/produits";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            produitRepository.deleteById(id);
            ra.addFlashAttribute("succes", "Produit supprimé.");
        } catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("erreur",
                    "Suppression impossible : ce produit apparaît dans des achats ou des livraisons.");
        }
        return "redirect:/produits";
    }
}