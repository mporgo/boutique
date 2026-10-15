package com.did.boutique.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Entity
public class Achat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateAchat = LocalDateTime.now();

    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "achat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneAchat> lignes = new ArrayList<>();

    public void ajouterLigne(LigneAchat ligne) {
        ligne.setAchat(this);
        lignes.add(ligne);
    }

    public void calculerTotal() {
        total = lignes.stream()
                .map(LigneAchat::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getDateFormatee() {
        return dateAchat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }
    public List<LigneAchat> getLignes() { return lignes; }
    public void setLignes(List<LigneAchat> lignes) { this.lignes = lignes; }
}