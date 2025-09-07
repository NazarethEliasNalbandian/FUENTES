package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HechoRepository {
    Hecho save(Hecho h);

    Optional<Hecho> findById(String id);

    List<Hecho> findAll();

    void deleteAll();

    // Consultas típicas que reemplazan a tu mapper en memoria
    List<Hecho> findByColeccionId(String coleccionId);

    List<Hecho> findByCategoria(CategoriaHechoEnum categoria);

    List<Hecho> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Hecho> findByTituloContainingIgnoreCase(String q);
}
