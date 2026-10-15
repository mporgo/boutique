package com.did.boutique.repository;

import com.did.boutique.model.LigneAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LigneAchatRepository extends JpaRepository<LigneAchat, Long> {

    @Query("select coalesce(sum((l.prixUnitaire - l.coutUnitaire) * l.quantite), 0) from LigneAchat l "
         + "where l.achat.dateAchat >= :debut and l.achat.dateAchat < :fin")
    BigDecimal beneficeEntre(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("select coalesce(sum((l.prixUnitaire - l.coutUnitaire) * l.quantite), 0) from LigneAchat l")
    BigDecimal beneficeGlobal();
}