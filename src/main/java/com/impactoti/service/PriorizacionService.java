package com.impactoti.service;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

@Service
public class PriorizacionService {

    private static final Map<String, String> PRIORIDADES =
            ImmutableMap.<String, String>builder()
                    .put("CRITICO", "ALTA")
                    .put("ALTO", "ALTA")
                    .put("MEDIO", "MEDIA")
                    .put("BAJO", "BAJA")
                    .build();

    private static final BigDecimal UMBRAL_MEDIO =
            new BigDecimal("500.00");
    private static final BigDecimal UMBRAL_BAJO =
            new BigDecimal("1000.00");

    public String calcularPrioridad(String nivelImpacto) {
        return calcularPrioridad(nivelImpacto, BigDecimal.ZERO);
    }

    public String calcularPrioridad(
            String nivelImpacto, BigDecimal impactoVentas) {

        if (StringUtils.isBlank(nivelImpacto)) {
            throw new IllegalArgumentException(
                    "El nivel de impacto es obligatorio.");
        }

        if (impactoVentas == null || impactoVentas.signum() < 0) {
            throw new IllegalArgumentException(
                    "El impacto en ventas no puede ser nulo ni negativo.");
        }

        String nivel = Normalizer.normalize(
                nivelImpacto.trim().toUpperCase(Locale.ROOT),
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");

        String prioridad = PRIORIDADES.get(nivel);

        if (prioridad == null) {
            throw new IllegalArgumentException(
                    "El nivel de impacto no es válido.");
        }

        if ("CRITICO".equals(nivel) || "ALTO".equals(nivel)) {
            return "ALTA";
        }

        if ("MEDIO".equals(nivel)
                && impactoVentas.compareTo(UMBRAL_MEDIO) >= 0) {
            return "ALTA";
        }

        if ("BAJO".equals(nivel)
                && impactoVentas.compareTo(UMBRAL_BAJO) >= 0) {
            return "MEDIA";
        }

        return prioridad;
    }
}