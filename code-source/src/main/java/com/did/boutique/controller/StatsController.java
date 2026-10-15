package com.did.boutique.controller;

import com.did.boutique.service.StatsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/api/stats")
    public Map<String, Object> stats(Authentication auth) {
        boolean gerant = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GERANT"));
        return statsService.calculer(gerant);
    }
}