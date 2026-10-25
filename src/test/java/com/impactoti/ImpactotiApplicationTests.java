
package com.impactoti;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ImpactotiApplicationTests {

    @Test
    void aplicacionImpactoTiTieneNombreCorrecto() {
        String nombreAplicacion = "ImpactoTI";

        assertTrue(nombreAplicacion.startsWith("Impacto"));
    }
}
