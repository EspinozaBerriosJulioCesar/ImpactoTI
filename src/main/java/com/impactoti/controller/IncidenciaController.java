
package com.impactoti.controller;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.impactoti.model.Equipo;
import com.impactoti.model.Incidencia;
import com.impactoti.model.Tienda;
import com.impactoti.model.Usuario;
import com.impactoti.repository.EquipoRepository;
import com.impactoti.repository.IncidenciaRepository;
import com.impactoti.repository.TiendaRepository;
import com.impactoti.repository.UsuarioRepository;
import com.impactoti.service.PriorizacionService;

@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    private static final Logger log =
            LoggerFactory.getLogger(IncidenciaController.class);

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("REGISTRADA", "EN_ATENCION", "RESUELTA");

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TiendaRepository tiendaRepository;
    private final EquipoRepository equipoRepository;
    private final PriorizacionService priorizacionService;

    public IncidenciaController(
            IncidenciaRepository incidenciaRepository,
            UsuarioRepository usuarioRepository,
            TiendaRepository tiendaRepository,
            EquipoRepository equipoRepository,
            PriorizacionService priorizacionService) {

        this.incidenciaRepository = incidenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tiendaRepository = tiendaRepository;
        this.equipoRepository = equipoRepository;
        this.priorizacionService = priorizacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "incidencias",
                incidenciaRepository.findAll());

        return "incidencias";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("equipos", equipoRepository.findAll());
        return "nueva-incidencia";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String nivelImpacto,
            @RequestParam(required = false) Long equipoId,
            @RequestParam(defaultValue = "0") BigDecimal impactoVentas,
            RedirectAttributes redirectAttributes) {

        if (titulo == null || titulo.isBlank()
                || descripcion == null || descripcion.isBlank()
                || nivelImpacto == null || nivelImpacto.isBlank()
                || equipoId == null
                || impactoVentas == null
                || impactoVentas.signum() < 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Completa los campos obligatorios y verifica el impacto en ventas.");

            return "redirect:/incidencias/nueva";
        }

        String prioridad;

        try {
            prioridad = priorizacionService.calcularPrioridad(nivelImpacto);
        } catch (IllegalArgumentException e) {
            log.warn("Intento de registrar incidencia con nivel de impacto inválido.");

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El nivel de impacto seleccionado no es válido.");

            return "redirect:/incidencias/nueva";
        }

        Usuario usuario = usuarioRepository.findById(1L).orElse(null);
        Tienda tienda = tiendaRepository.findById(1L).orElse(null);
        Equipo equipo = equipoRepository.findById(equipoId).orElse(null);

        if (usuario == null || tienda == null) {
            log.error("No se encontró el usuario o la tienda de prueba.");

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró el usuario o la tienda de prueba en la base de datos.");

            return "redirect:/incidencias/nueva";
        }

        if (equipo == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El equipo seleccionado no existe.");

            return "redirect:/incidencias/nueva";
        }

        if (equipo.getTienda() == null
                || !tienda.getId().equals(equipo.getTienda().getId())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El equipo seleccionado no pertenece a la tienda asignada.");

            return "redirect:/incidencias/nueva";
        }

        Incidencia incidencia = new Incidencia();
        incidencia.setTitulo(titulo.trim());
        incidencia.setDescripcion(descripcion.trim());
        incidencia.setNivelImpacto(
                nivelImpacto.trim().toUpperCase(Locale.ROOT));
        incidencia.setImpactoVentas(impactoVentas);
        incidencia.setPrioridad(prioridad);
        incidencia.setEstado("REGISTRADA");
        incidencia.setUsuario(usuario);
        incidencia.setTienda(tienda);
        incidencia.setEquipo(equipo);

        Incidencia guardada = incidenciaRepository.save(incidencia);

        log.info(
                "Incidencia registrada. ID={}, prioridad={}, estado={}",
                guardada.getId(),
                guardada.getPrioridad(),
                guardada.getEstado());

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Incidencia registrada correctamente.");

        return "redirect:/incidencias";
    }

    @PostMapping("/actualizar-estado")
    public String actualizarEstado(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String estado,
            RedirectAttributes redirectAttributes) {

        if (id == null || estado == null
                || !ESTADOS_VALIDOS.contains(estado)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El identificador o el estado seleccionado no es válido.");

            return "redirect:/incidencias";
        }

        Incidencia incidencia =
                incidenciaRepository.findById(id).orElse(null);

        if (incidencia == null) {
            log.warn("No se encontró la incidencia con ID={}", id);

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró la incidencia indicada.");

            return "redirect:/incidencias";
        }

        incidencia.setEstado(estado);

        Incidencia actualizada = incidenciaRepository.save(incidencia);

        log.info(
                "Estado de incidencia actualizado. ID={}, nuevo estado={}",
                actualizada.getId(),
                actualizada.getEstado());

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Estado de la incidencia actualizado correctamente.");

        return "redirect:/incidencias";
    }
}
