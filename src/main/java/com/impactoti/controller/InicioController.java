
package com.impactoti.controller;

import com.impactoti.model.Incidencia;
import com.impactoti.repository.IncidenciaRepository;
import java.util.Comparator;
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
                    String prioridad = normalizar(i.getPrioridad());
                    String estado = normalizar(i.getEstado());

                    boolean prioridadCritica =
                            prioridad.equals("ALTA")
                            || prioridad.equals("CRITICA")
                            || prioridad.equals("CRITICO");

                    return prioridadCritica && !estado.equals("RESUELTA");
                })
                .count();

        List<Incidencia> ultimasIncidencias = incidencias.stream()
                .sorted(Comparator.comparing(
                        Incidencia::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        model.addAttribute("totalIncidencias", total);
        model.addAttribute("incidenciasRegistradas", registradas);
        model.addAttribute("incidenciasCriticas", criticas);
        model.addAttribute("incidenciasEnAtencion", enAtencion);
        model.addAttribute("incidenciasResueltas", resueltas);
        model.addAttribute("ultimasIncidencias", ultimasIncidencias);

        return "index";
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return java.text.Normalizer.normalize(
                valor.trim().toUpperCase(Locale.ROOT),
                java.text.Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");
    }
}
