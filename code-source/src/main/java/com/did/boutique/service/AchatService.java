package com.did.boutique.service;

import com.did.boutique.model.Achat;
import com.did.boutique.model.Client;
import com.did.boutique.model.LigneAchat;
import com.did.boutique.model.Produit;
import com.did.boutique.model.Utilisateur;
import com.did.boutique.repository.AchatRepository;
import com.did.boutique.repository.ClientRepository;
import com.did.boutique.repository.ProduitRepository;
import com.did.boutique.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AchatService {

    private final AchatRepository achatRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;

    public AchatService(AchatRepository achatRepository, ProduitRepository produitRepository,
                        UtilisateurRepository utilisateurRepository, ClientRepository clientRepository) {
        this.achatRepository = achatRepository;
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Achat enregistrerAchat(String identifiant, String nomClient, String telephoneClient,
                                  List<Long> produitIds, List<Integer> quantites) {

        // Regroupe les lignes : si un produit apparaît deux fois, on additionne les quantités
        Map<Long, Integer> demandes = new LinkedHashMap<>();
        if (produitIds != null && quantites != null) {
            for (int i = 0; i < produitIds.size() && i < quantites.size(); i++) {
                Long pid = produitIds.get(i);
                Integer q = quantites.get(i);
                if (pid != null && q != null && q > 0) {
                    demandes.merge(pid, q, Integer::sum);
                }
            }
        }
        if (demandes.isEmpty()) {
            throw new IllegalArgumentException("Ajoutez au moins un produit avec une quantité valide.");
        }

        Utilisateur caissier = utilisateurRepository.findByIdentifiant(identifiant)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur connecté introuvable."));

        Achat achat = new Achat();
        achat.setUtilisateur(caissier);

        if (nomClient != null && !nomClient.isBlank()) {
            Client client = new Client();
            client.setNom(nomClient.trim());
            if (telephoneClient != null && !telephoneClient.isBlank()) {
                client.setTelephone(telephoneClient.trim());
            }
            achat.setClient(clientRepository.save(client));
        }

        for (Map.Entry<Long, Integer> e : demandes.entrySet()) {
            Produit produit = produitRepository.findById(e.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Produit introuvable."));
            int quantite = e.getValue();

            if (produit.getStock() < quantite) {
                throw new IllegalArgumentException("Stock insuffisant pour « " + produit.getNom()
                        + " » (disponible : " + produit.getStock() + ").");
            }
            produit.setStock(produit.getStock() - quantite);

            LigneAchat ligne = new LigneAchat();
            ligne.setProduit(produit);
            ligne.setQuantite(quantite);
            ligne.setPrixUnitaire(produit.getPrixVente());
            ligne.setCoutUnitaire(produit.getPrixAchatMoyen());
            achat.ajouterLigne(ligne);
        }

        achat.calculerTotal();
        return achatRepository.save(achat);
    }
}