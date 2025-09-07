package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Coleccion;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@Profile("!test")
public interface JpaColeccionRepository
        extends JpaRepository<Coleccion, String>, ColeccionRepository {

    // Spring Data implementa estos métodos por convención
    boolean existsByNombre(String nombre);

    Optional<Coleccion> findByNombre(String nombre);
}

