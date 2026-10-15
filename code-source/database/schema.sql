CREATE DATABASE IF NOT EXISTS boutique
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE boutique;

CREATE TABLE utilisateur (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom            VARCHAR(100) NOT NULL,
  prenom         VARCHAR(100) NOT NULL,
  matricule      VARCHAR(30)  NOT NULL UNIQUE,
  sexe           ENUM('M','F') NOT NULL,
  date_naissance DATE NOT NULL,
  identifiant    VARCHAR(50)  NOT NULL UNIQUE,
  mot_de_passe   VARCHAR(255) NOT NULL,          -- hash BCrypt
  role           ENUM('GERANT','CAISSIER') NOT NULL DEFAULT 'CAISSIER',
  actif          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE categorie (
  id  BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE produit (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom            VARCHAR(150) NOT NULL,
  prix_vente     DECIMAL(12,2) NOT NULL CHECK (prix_vente >= 0),
  stock          INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  prix_achat_moyen DECIMAL(12,2) NOT NULL DEFAULT 0,
  seuil_alerte   INT NOT NULL DEFAULT 5,
  categorie_id   BIGINT NULL,
  CONSTRAINT fk_produit_categorie FOREIGN KEY (categorie_id) REFERENCES categorie(id),
  INDEX idx_produit_nom (nom)
) ENGINE=InnoDB;

CREATE TABLE client (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom       VARCHAR(100) NOT NULL,
  telephone VARCHAR(30)
) ENGINE=InnoDB;

CREATE TABLE grossiste (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  nom       VARCHAR(150) NOT NULL,
  telephone VARCHAR(30),
  adresse   VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE achat (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  date_achat     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total          DECIMAL(14,2) NOT NULL DEFAULT 0,
  client_id      BIGINT NULL,
  utilisateur_id BIGINT NOT NULL,
  CONSTRAINT fk_achat_client FOREIGN KEY (client_id) REFERENCES client(id),
  CONSTRAINT fk_achat_user   FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
) ENGINE=InnoDB;

CREATE TABLE ligne_achat (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  achat_id      BIGINT NOT NULL,
  produit_id    BIGINT NOT NULL,
  quantite      INT NOT NULL CHECK (quantite > 0),
  prix_unitaire DECIMAL(12,2) NOT NULL,
  cout_unitaire DECIMAL(12,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_la_achat   FOREIGN KEY (achat_id)   REFERENCES achat(id) ON DELETE CASCADE,
  CONSTRAINT fk_la_produit FOREIGN KEY (produit_id) REFERENCES produit(id)
) ENGINE=InnoDB;

CREATE TABLE livraison (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  date_livraison DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total          DECIMAL(14,2) NOT NULL DEFAULT 0,
  grossiste_id   BIGINT NOT NULL,
  utilisateur_id BIGINT NOT NULL,
  CONSTRAINT fk_liv_grossiste FOREIGN KEY (grossiste_id)   REFERENCES grossiste(id),
  CONSTRAINT fk_liv_user      FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
) ENGINE=InnoDB;

CREATE TABLE ligne_livraison (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  livraison_id  BIGINT NOT NULL,
  produit_id    BIGINT NOT NULL,
  quantite      INT NOT NULL CHECK (quantite > 0),
  prix_achat    DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_ll_livraison FOREIGN KEY (livraison_id) REFERENCES livraison(id) ON DELETE CASCADE,
  CONSTRAINT fk_ll_produit   FOREIGN KEY (produit_id)   REFERENCES produit(id)
) ENGINE=InnoDB;