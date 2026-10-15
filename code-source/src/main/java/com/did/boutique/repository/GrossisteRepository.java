package com.did.boutique.repository;

import com.did.boutique.model.Grossiste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrossisteRepository extends JpaRepository<Grossiste, Long> {
}