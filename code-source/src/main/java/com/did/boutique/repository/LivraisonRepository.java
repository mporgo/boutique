package com.did.boutique.repository;

import com.did.boutique.model.Livraison;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    @EntityGraph(attributePaths = {"lignes", "lignes.produit", "grossiste", "utilisateur"})
    List<Livraison> findAllByOrderByDateLivraisonDesc();

    @EntityGraph(attributePaths = {"lignes", "lignes.produit", "grossiste", "utilisateur"})
    Optional<Livraison> findWithLignesById(Long id);
}