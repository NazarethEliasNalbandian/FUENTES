package ar.edu.utn.dds.k3003.repositories.jpa;

import ar.edu.utn.dds.k3003.model.Coleccion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!test")
public interface SpringDataColeccionJpa
        extends org.springframework.data.jpa.repository.JpaRepository<Coleccion, String> {

    boolean existsByNombre(String nombre);
    Optional<Coleccion> findByNombre(String nombre);
}

