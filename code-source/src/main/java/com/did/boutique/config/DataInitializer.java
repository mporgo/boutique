package com.did.boutique.config;

import com.did.boutique.model.Role;
import com.did.boutique.model.Sexe;
import com.did.boutique.model.Utilisateur;
import com.did.boutique.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() == 0) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Administrateur");
            admin.setPrenom("Gérant");
            admin.setMatricule("ADM001");
            admin.setSexe(Sexe.M);
            admin.setDateNaissance(LocalDate.of(1990, 1, 1));
            admin.setIdentifiant("admin");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            admin.setRole(Role.GERANT);
            utilisateurRepository.save(admin);
            System.out.println(">>> Compte gérant créé : admin / admin123");
        }
    }
}