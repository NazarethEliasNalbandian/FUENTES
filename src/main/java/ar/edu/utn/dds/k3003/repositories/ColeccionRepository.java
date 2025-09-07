package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColeccionRepository extends JpaRepository<Coleccion, String> {
    boolean existsByNombre(String nombre);
}
