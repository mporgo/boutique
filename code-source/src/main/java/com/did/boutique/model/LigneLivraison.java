package com.did.boutique.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class LigneLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private int quantite;

    private BigDecimal prixAchat;

    @Transient
    public BigDecimal getSousTotal() {
        return prixAchat.multiply(BigDecimal.valueOf(quantite));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Livraison getLivraison() { return livraison; }
    public void setLivraison(Livraison livraison) { this.livraison = livraison; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public BigDecimal getPrixAchat() { return prixAchat; }
    public void setPrixAchat(BigDecimal prixAchat) { this.prixAchat = prixAchat; }
}