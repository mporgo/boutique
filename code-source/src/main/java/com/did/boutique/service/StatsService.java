package com.did.boutique.service;

import com.did.boutique.repository.AchatRepository;
import com.did.boutique.repository.LigneAchatRepository;
import com.did.boutique.repository.ProduitRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StatsService {

    private final AchatRepository achatRepository;
    private final LigneAchatRepository ligneAchatRepository;
    private final ProduitRepository produitRepository;

    public StatsService(AchatRepository achatRepository, LigneAchatRepository ligneAchatRepository,
                        ProduitRepository produitRepository) {
        this.achatRepository = achatRepository;
        this.ligneAchatRepository = ligneAchatRepository;
        this.produitRepository = produitRepository;
    }

    public Map<String, Object> calculer(boolean gerant) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDateTime debutJour = aujourdhui.atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1);
        LocalDateTime debutMois = aujourdhui.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMois = debutMois.plusMonths(1);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("caJour", achatRepository.totalEntre(debutJour, finJour));
        stats.put("caMois", achatRepository.totalEntre(debutMois, finMois));
        stats.put("caTotal", achatRepository.totalGlobal());
        stats.put("nbAchatsJour", achatRepository.compterEntre(debutJour, finJour));
        stats.put("nbProduits", produitRepository.count());
        stats.put("nbStockBas", produitRepository.findStockBas().size());

        if (gerant) {
            stats.put("beneficeJour", ligneAchatRepository.beneficeEntre(debutJour, finJour));
            stats.put("beneficeMois", ligneAchatRepository.beneficeEntre(debutMois, finMois));
            stats.put("beneficeTotal", ligneAchatRepository.beneficeGlobal());
        }
        return stats;
    }
}