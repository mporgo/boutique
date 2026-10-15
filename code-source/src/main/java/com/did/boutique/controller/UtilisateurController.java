package com.did.boutique.controller;

import com.did.boutique.dto.UtilisateurForm;
import com.did.boutique.model.Role;
import com.did.boutique.model.Sexe;
import com.did.boutique.repository.UtilisateurRepository;
import com.did.boutique.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurService utilisateurService,
                                 UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @ModelAttribute("sexes")
    public Sexe[] sexes() {
        return Sexe.values();
    }

    @ModelAttribute("roles")
    public Role[] roles() {
        return Role.values();
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll(Sort.by("nom", "prenom")));
        return "utilisateurs/liste";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return utilisateurRepository.findById(id)
                .map(u -> {
                    model.addAttribute("utilisateur", u);
                    return "utilisateurs/details";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erreur", "Utilisateur introuvable.");
                    return "redirect:/utilisateurs";
                });
    }

    @GetMapping("/nouveau")
    public String nouveau(Model model) {
        model.addAttribute("form", new UtilisateurForm());
        return "utilisateurs/form";
    }

    @PostMapping
    public String creer(@Valid @ModelAttribute("form") UtilisateurForm form,
                        BindingResult result, RedirectAttributes ra) {

        if (form.getMotDePasse() != null && !form.getMotDePasse().equals(form.getConfirmation())) {
            result.rejectValue("confirmation", "mismatch", "Les mots de passe ne correspondent pas.");
        }
        if (utilisateurService.identifiantExiste(form.getIdentifiant())) {
            result.rejectValue("identifiant", "duplicate", "Cet identifiant est déjà utilisé.");
        }
        if (utilisateurService.matriculeExiste(form.getMatricule())) {
            result.rejectValue("matricule", "duplicate", "Ce matricule existe déjà.");
        }
        if (result.hasErrors()) {
            return "utilisateurs/form";
        }

        utilisateurService.creer(form);
        ra.addFlashAttribute("succes", "Utilisateur « " + form.getIdentifiant().trim() + " » créé.");
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/basculer")
    public String basculer(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        try {
            utilisateurService.basculerActif(id, auth.getName());
            ra.addFlashAttribute("succes", "Statut du compte mis à jour.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/utilisateurs";
    }
}