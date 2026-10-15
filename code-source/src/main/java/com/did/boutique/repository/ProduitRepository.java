package com.did.boutique.repository;

import com.did.boutique.model.Produit;
import com.did.boutique.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    // Recherche d'un produit par nom (partiel, sans tenir compte de la casse)
    List<Produit> findByNomContainingIgnoreCase(String nom);

    // Compte le nombre de produits dans une catégorie donnée
    long countByCategorie(Categorie categorie);

    // Produits dont le stock est sous le seuil d'alerte
    @Query("select p from Produit p where p.stock <= p.seuilAlerte")
    List<Produit> findStockBas();
}