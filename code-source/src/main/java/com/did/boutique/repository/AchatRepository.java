package com.did.boutique.repository;

import com.did.boutique.model.Achat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AchatRepository extends JpaRepository<Achat, Long> {

    @EntityGraph(attributePaths = {"lignes", "lignes.produit", "client", "utilisateur"})
    List<Achat> findAllByOrderByDateAchatDesc();

    @EntityGraph(attributePaths = {"lignes", "lignes.produit", "client", "utilisateur"})
    Optional<Achat> findWithLignesById(Long id);

    @Query("select coalesce(sum(a.total), 0) from Achat a")
    BigDecimal totalGlobal();

    @Query("select count(a) from Achat a where a.dateAchat >= :debut and a.dateAchat < :fin")
    long compterEntre(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("select coalesce(sum(a.total), 0) from Achat a where a.dateAchat >= :debut and a.dateAchat < :fin")
    BigDecimal totalEntre(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}