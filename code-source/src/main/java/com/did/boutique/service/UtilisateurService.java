package com.did.boutique.service;

import com.did.boutique.dto.UtilisateurForm;
import com.did.boutique.model.Utilisateur;
import com.did.boutique.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean identifiantExiste(String identifiant) {
        return identifiant != null && utilisateurRepository.existsByIdentifiant(identifiant.trim());
    }

    public boolean matriculeExiste(String matricule) {
        return matricule != null && utilisateurRepository.existsByMatricule(matricule.trim());
    }

    @Transactional
    public Utilisateur creer(UtilisateurForm f) {
        Utilisateur u = new Utilisateur();
        u.setNom(f.getNom().trim());
        u.setPrenom(f.getPrenom().trim());
        u.setMatricule(f.getMatricule().trim());
        u.setSexe(f.getSexe());
        u.setDateNaissance(f.getDateNaissance());
        u.setIdentifiant(f.getIdentifiant().trim());
        u.setMotDePasse(passwordEncoder.encode(f.getMotDePasse()));
        u.setRole(f.getRole());
        u.setActif(true);
        return utilisateurRepository.save(u);
    }

    @Transactional
    public Utilisateur basculerActif(Long id, String identifiantCourant) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        if (u.getIdentifiant().equals(identifiantCourant)) {
            throw new IllegalArgumentException("Vous ne pouvez pas désactiver votre propre compte.");
        }
        u.setActif(!u.isActif());
        return u;
    }
}