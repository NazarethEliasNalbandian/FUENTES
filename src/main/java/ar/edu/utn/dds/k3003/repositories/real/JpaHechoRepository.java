// Implementación JPA
package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("!test")
public interface JpaHechoRepository
        extends JpaRepository<Hecho, String>, HechoRepository {

    // Derivadas por nombre (no necesitas @Query)
    List<Hecho> findByColeccionId(String coleccionId);

    List<Hecho> findByCategoria(CategoriaHechoEnum categoria);

    List<Hecho> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Hecho> findByTituloContainingIgnoreCase(String q);
}
