package com.impactoti.service;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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

    public String calcularPrioridad(String nivelImpacto) {
        if (StringUtils.isBlank(nivelImpacto)) {
            throw new IllegalArgumentException(
                    "El nivel de impacto es obligatorio.");
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

        return prioridad;
    }
}