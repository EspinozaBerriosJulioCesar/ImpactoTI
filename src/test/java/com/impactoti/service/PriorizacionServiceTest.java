package com.impactoti.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorizacionServiceTest {

    private final PriorizacionService servicio =
            new PriorizacionService();

    @Test
    void nivelCriticoDebeSerPrioridadAlta() {
        assertEquals("ALTA", servicio.calcularPrioridad("CRITICO"));
        assertEquals("ALTA", servicio.calcularPrioridad("CRÍTICO"));
    }

    @Test
    void nivelMedioDebeSerPrioridadMedia() {
        assertEquals("MEDIA", servicio.calcularPrioridad("MEDIO"));
    }

    @Test
    void nivelBajoDebeSerPrioridadBaja() {
        assertEquals("BAJA", servicio.calcularPrioridad("BAJO"));
    }

    @Test
    void nivelInvalidoDebeLanzarExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularPrioridad("URGENTE")
        );
    }
}