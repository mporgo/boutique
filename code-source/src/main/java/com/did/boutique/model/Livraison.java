package com.did.boutique.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Entity
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateLivraison = LocalDateTime.now();

    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grossiste_id")
    private Grossiste grossiste;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "livraison", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneLivraison> lignes = new ArrayList<>();

    public void ajouterLigne(LigneLivraison ligne) {
        ligne.setLivraison(this);
        lignes.add(ligne);
    }

    public void calculerTotal() {
        total = lignes.stream()
                .map(LigneLivraison::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getDateFormatee() {
        return dateLivraison.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateLivraison() { return dateLivraison; }
    public void setDateLivraison(LocalDateTime dateLivraison) { this.dateLivraison = dateLivraison; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Grossiste getGrossiste() { return grossiste; }
    public void setGrossiste(Grossiste grossiste) { this.grossiste = grossiste; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public List<LigneLivraison> getLignes() { return lignes; }
    public void setLignes(List<LigneLivraison> lignes) { this.lignes = lignes; }
}