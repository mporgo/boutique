package com.did.boutique.controller;

import com.did.boutique.model.Categorie;
import com.did.boutique.repository.CategorieRepository;
import com.did.boutique.repository.ProduitRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CategorieController {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public CategorieController(CategorieRepository categorieRepository, ProduitRepository produitRepository) {
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("categorie", new Categorie());
        remplir(model);
        return "categories/liste";
    }

    @PostMapping
    public String creer(@ModelAttribute("categorie") Categorie saisie, BindingResult result,
                        Model model, RedirectAttributes ra) {

        String nom = saisie.getNom() == null ? "" : saisie.getNom().trim();

        if (nom.isEmpty()) {
            result.rejectValue("nom", "blank", "Le nom de la catégorie est obligatoire.");
        } else if (nom.length() > 100) {
            result.rejectValue("nom", "size", "Le nom ne doit pas dépasser 100 caractères.");
        } else if (categorieRepository.existsByNomIgnoreCase(nom)) {
            result.rejectValue("nom", "duplicate", "Cette catégorie existe déjà.");
        }

        if (!result.hasErrors()) {
            try {
                Categorie c = new Categorie();
                c.setNom(nom);
                categorieRepository.save(c);
                ra.addFlashAttribute("succes", "Catégorie « " + nom + " » créée.");
                return "redirect:/categories";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("nom", "duplicate", "Cette catégorie existe déjà.");
            }
        }

        remplir(model);
        return "categories/liste";
    }

    private void remplir(Model model) {
        List<Categorie> categories = categorieRepository.findAll(Sort.by("nom"));
        Map<Long, Long> nbProduits = new LinkedHashMap<>();
        for (Categorie c : categories) {
            nbProduits.put(c.getId(), produitRepository.countByCategorie(c));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("nbProduits", nbProduits);
    }
}