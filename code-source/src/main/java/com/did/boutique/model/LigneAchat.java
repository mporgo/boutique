package com.did.boutique.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class LigneAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "achat_id")
    private Achat achat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produit_id")
    private Produit produit;

    private int quantite;

    private BigDecimal prixUnitaire;

    private BigDecimal coutUnitaire = BigDecimal.ZERO;

    @Transient
    public BigDecimal getSousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Achat getAchat() { return achat; }
    public void setAchat(Achat achat) { this.achat = achat; }
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public BigDecimal getCoutUnitaire() { return coutUnitaire; }
    public void setCoutUnitaire(BigDecimal coutUnitaire) { this.coutUnitaire = coutUnitaire; }

    @Transient
    public BigDecimal getBenefice() {
        return prixUnitaire.subtract(coutUnitaire).multiply(BigDecimal.valueOf(quantite));
    }
}