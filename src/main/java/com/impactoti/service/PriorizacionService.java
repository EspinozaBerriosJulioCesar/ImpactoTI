package com.impactoti.service;

import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.Locale;

@Service
public class PriorizacionService {

    public String calcularPrioridad(String nivelImpacto) {
        if (nivelImpacto == null || nivelImpacto.isBlank()) {
            throw new IllegalArgumentException(
                    "El nivel de impacto es obligatorio.");
        }

        String nivel = Normalizer.normalize(
                nivelImpacto.trim().toUpperCase(Locale.ROOT),
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");

        return switch (nivel) {
            case "CRITICO", "ALTO" -> "ALTA";
            case "MEDIO" -> "MEDIA";
            case "BAJO" -> "BAJA";
            default -> throw new IllegalArgumentException(
                    "El nivel de impacto no es valido.");
        };
    }
}