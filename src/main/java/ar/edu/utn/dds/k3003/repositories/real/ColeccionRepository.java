// Dominio-agnóstico (para usarlo en Services)
package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Coleccion;
import java.util.List;
import java.util.Optional;

public interface ColeccionRepository {
    Coleccion save(Coleccion c);

    Optional<Coleccion> findById(String id);

    List<Coleccion> findAll();

    void deleteAll();

    // Consultas de negocio que vas a querer desde el Service
    boolean existsByNombre(String nombre);

    Optional<Coleccion> findByNombre(String nombre);
}
