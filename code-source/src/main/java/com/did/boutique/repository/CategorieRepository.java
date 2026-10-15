package com.did.boutique.repository;

import com.did.boutique.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    boolean existsByNomIgnoreCase(String nom);
}