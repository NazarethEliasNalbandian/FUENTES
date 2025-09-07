// Dominio-agnóstico (para usarlo en Services)
package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Coleccion;
import java.util.List;
import java.util.Optional;

public interface ColRepository {
    Coleccion save(Coleccion c);

    Coleccion findById(String id);

    List<Coleccion> allColeccciones();

    void delete(Coleccion c);

    void deleteAll();
    int count();

    // Consultas de negocio que vas a querer desde el Service
    boolean existsByNombre(String nombre);

    Optional<Coleccion> findByNombre(String nombre);
}
