package com.did.boutique.service;

import com.did.boutique.model.Grossiste;
import com.did.boutique.model.LigneLivraison;
import com.did.boutique.model.Livraison;
import com.did.boutique.model.Produit;
import com.did.boutique.model.Utilisateur;
import com.did.boutique.repository.GrossisteRepository;
import com.did.boutique.repository.LivraisonRepository;
import com.did.boutique.repository.ProduitRepository;
import com.did.boutique.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.math.RoundingMode;

@Service
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final ProduitRepository produitRepository;
    private final GrossisteRepository grossisteRepository;
    private final UtilisateurRepository utilisateurRepository;

    public LivraisonService(LivraisonRepository livraisonRepository, ProduitRepository produitRepository,
                            GrossisteRepository grossisteRepository, UtilisateurRepository utilisateurRepository) {
        this.livraisonRepository = livraisonRepository;
        this.produitRepository = produitRepository;
        this.grossisteRepository = grossisteRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public Livraison enregistrerLivraison(String identifiant, Long grossisteId, String nomGrossiste,
                                          String telephoneGrossiste, List<Long> produitIds,
                                          List<Integer> quantites, List<BigDecimal> prixAchats) {

        Utilisateur utilisateur = utilisateurRepository.findByIdentifiant(identifiant)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur connecté introuvable."));

        Grossiste grossiste;
        if (grossisteId != null) {
            grossiste = grossisteRepository.findById(grossisteId)
                    .orElseThrow(() -> new IllegalArgumentException("Grossiste introuvable."));
        } else if (nomGrossiste != null && !nomGrossiste.isBlank()) {
            Grossiste g = new Grossiste();
            g.setNom(nomGrossiste.trim());
            if (telephoneGrossiste != null && !telephoneGrossiste.isBlank()) {
                g.setTelephone(telephoneGrossiste.trim());
            }
            grossiste = grossisteRepository.save(g);
        } else {
            throw new IllegalArgumentException("Choisissez un grossiste ou saisissez le nom d'un nouveau.");
        }

        Livraison livraison = new Livraison();
        livraison.setGrossiste(grossiste);
        livraison.setUtilisateur(utilisateur);

        int nbLignes = 0;
        if (produitIds != null && quantites != null && prixAchats != null) {
            int n = Math.min(produitIds.size(), Math.min(quantites.size(), prixAchats.size()));
            for (int i = 0; i < n; i++) {
                Long pid = produitIds.get(i);
                Integer q = quantites.get(i);
                BigDecimal prix = prixAchats.get(i);
                if (pid == null || q == null || q <= 0 || prix == null || prix.signum() < 0) {
                    continue;
                }
                Produit produit = produitRepository.findById(pid)
                        .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));
                int ancienStock = produit.getStock();
                BigDecimal ancienneValeur = produit.getPrixAchatMoyen().multiply(BigDecimal.valueOf(ancienStock));
                BigDecimal valeurRecue = prix.multiply(BigDecimal.valueOf(q));
                int nouveauStock = ancienStock + q;
                produit.setPrixAchatMoyen(ancienneValeur.add(valeurRecue)
                        .divide(BigDecimal.valueOf(nouveauStock), 2, RoundingMode.HALF_UP));
                produit.setStock(nouveauStock);

                LigneLivraison ligne = new LigneLivraison();
                ligne.setProduit(produit);
                ligne.setQuantite(q);
                ligne.setPrixAchat(prix);
                livraison.ajouterLigne(ligne);
                nbLignes++;
            }
        }
        if (nbLignes == 0) {
            throw new IllegalArgumentException("Ajoutez au moins un produit avec une quantité et un prix d'achat valides.");
        }

        livraison.calculerTotal();
        return livraisonRepository.save(livraison);
    }
}