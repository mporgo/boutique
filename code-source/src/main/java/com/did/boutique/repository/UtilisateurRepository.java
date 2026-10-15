package com.did.boutique.repository;

import com.did.boutique.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByIdentifiant(String identifiant);
    boolean existsByIdentifiant(String identifiant);
    boolean existsByMatricule(String matricule);
}