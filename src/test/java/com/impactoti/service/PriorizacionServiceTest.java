package com.impactoti.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriorizacionServiceTest {

    private final PriorizacionService servicio =
            new PriorizacionService();

    @Test
    void nivelCriticoDebeSerPrioridadAlta() {
        assertEquals("ALTA", servicio.calcularPrioridad("CRÍTICO"));
    }

    @Test
    void nivelAltoDebeSerPrioridadAlta() {
        assertEquals("ALTA", servicio.calcularPrioridad("ALTO"));
    }

    @Test
    void nivelMedioConImpactoElevadoDebeSerAlta() {
        assertEquals("ALTA", servicio.calcularPrioridad(
                "MEDIO", new BigDecimal("500.00")));
    }

    @Test
    void nivelMedioConImpactoMenorDebeSerMedia() {
        assertEquals("MEDIA", servicio.calcularPrioridad(
                "MEDIO", new BigDecimal("499.99")));
    }

    @Test
    void nivelBajoConImpactoElevadoDebeSerMedia() {
        assertEquals("MEDIA", servicio.calcularPrioridad(
                "BAJO", new BigDecimal("1000.00")));
    }

    @Test
    void nivelBajoConImpactoMenorDebeSerBaja() {
        assertEquals("BAJA", servicio.calcularPrioridad(
                "BAJO", new BigDecimal("999.99")));
    }

    @Test
    void nivelInvalidoDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularPrioridad("URGENTE"));
    }

    @Test
    void impactoNegativoDebeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularPrioridad(
                        "MEDIO", new BigDecimal("-1.00")));
    }
}