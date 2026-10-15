package com.did.boutique.service;

import com.did.boutique.model.Utilisateur;
import com.did.boutique.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        Utilisateur u = utilisateurRepository.findByIdentifiant(identifiant)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + identifiant));

        return User.withUsername(u.getIdentifiant())
                .password(u.getMotDePasse())
                .roles(u.getRole().name())
                .disabled(!u.isActif())
                .build();
    }
}