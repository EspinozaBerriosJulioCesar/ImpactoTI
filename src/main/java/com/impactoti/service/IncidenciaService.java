package com.impactoti.service;

import com.impactoti.model.Equipo;
import com.impactoti.model.Incidencia;
import com.impactoti.model.Tienda;
import com.impactoti.model.Usuario;
import com.impactoti.repository.IncidenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final PriorizacionService priorizacionService;

    public IncidenciaService(
            IncidenciaRepository incidenciaRepository,
            PriorizacionService priorizacionService) {
        this.incidenciaRepository = incidenciaRepository;
        this.priorizacionService = priorizacionService;
    }

    @Transactional
    public Incidencia registrar(
            String titulo,
            String descripcion,
            String nivelImpacto,
            BigDecimal impactoVentas,
            Usuario usuario,
            Tienda tienda,
            Equipo equipo) {

        if (titulo == null || titulo.isBlank()
                || descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "El título y la descripción son obligatorios.");
        }

        if (nivelImpacto == null || nivelImpacto.isBlank()) {
            throw new IllegalArgumentException(
                    "El nivel de impacto es obligatorio.");
        }

        if (impactoVentas == null || impactoVentas.signum() < 0) {
            throw new IllegalArgumentException(
                    "El impacto en ventas no puede ser negativo.");
        }

        if (usuario == null || tienda == null || equipo == null) {
            throw new IllegalArgumentException(
                    "El usuario, la tienda y el equipo son obligatorios.");
        }

        Incidencia incidencia = new Incidencia();
        incidencia.setTitulo(titulo.trim());
        incidencia.setDescripcion(descripcion.trim());
        incidencia.setNivelImpacto(nivelImpacto.trim());
        incidencia.setImpactoVentas(impactoVentas);
        incidencia.setPrioridad(
                priorizacionService.calcularPrioridad(nivelImpacto));
        incidencia.setEstado("REGISTRADA");
        incidencia.setUsuario(usuario);
        incidencia.setTienda(tienda);
        incidencia.setEquipo(equipo);

        return incidenciaRepository.save(incidencia);
    }
}
