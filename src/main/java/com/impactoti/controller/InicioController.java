package com.impactoti.controller;

import com.impactoti.model.Incidencia;
import com.impactoti.repository.IncidenciaRepository;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final IncidenciaRepository incidenciaRepository;

    public InicioController(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        List<Incidencia> incidencias = incidenciaRepository.findAll();

        long total = incidencias.size();

        long registradas = incidencias.stream()
                .filter(i -> "REGISTRADA".equalsIgnoreCase(i.getEstado()))
                .count();

        long enAtencion = incidencias.stream()
                .filter(i -> "EN_ATENCION".equalsIgnoreCase(i.getEstado()))
                .count();

        long resueltas = incidencias.stream()
                .filter(i -> "RESUELTA".equalsIgnoreCase(i.getEstado()))
                .count();

        long criticas = incidencias.stream()
                .filter(i -> {
                    String prioridad = i.getPrioridad() == null
                            ? ""
                            : i.getPrioridad().trim().toUpperCase(Locale.ROOT);

                    String estado = i.getEstado() == null
                            ? ""
                            : i.getEstado().trim().toUpperCase(Locale.ROOT);

                    boolean prioridadCritica =
                            prioridad.equals("CRITICA")
                            || prioridad.equals("CRÍTICA")
                            || prioridad.equals("CRITICO")
                            || prioridad.equals("CRÍTICO");

                    return prioridadCritica && !estado.equals("RESUELTA");
                })
                .count();

        model.addAttribute("totalIncidencias", total);
        model.addAttribute("incidenciasRegistradas", registradas);
        model.addAttribute("incidenciasCriticas", criticas);
        model.addAttribute("incidenciasEnAtencion", enAtencion);
        model.addAttribute("incidenciasResueltas", resueltas);

        model.addAttribute("ultimasIncidencias",
                incidencias.stream()
                        .sorted((a, b) -> b.getId().compareTo(a.getId()))
                        .limit(5)
                        .toList());

        return "index";
    }
}