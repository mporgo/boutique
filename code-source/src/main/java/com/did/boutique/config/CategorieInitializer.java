package com.did.boutique.config;

import com.did.boutique.model.Categorie;
import com.did.boutique.repository.CategorieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CategorieInitializer implements CommandLineRunner {

    private final CategorieRepository categorieRepository;

    public CategorieInitializer(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public void run(String... args) {
        if (categorieRepository.count() == 0) {
            for (String nom : List.of("Céréales et farines", "Huiles et condiments", "Boissons",
                    "Produits laitiers", "Conserves", "Épicerie sucrée", "Autres")) {
                Categorie c = new Categorie();
                c.setNom(nom);
                categorieRepository.save(c);
            }
        }
    }
}