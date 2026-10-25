
package com.impactoti.repository;

import com.impactoti.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByTiendaId(Long tiendaId);
}
