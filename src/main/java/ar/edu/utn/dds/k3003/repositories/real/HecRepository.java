package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;

import java.time.LocalDateTime;
import java.util.List;

public interface HecRepository {
    Hecho save(Hecho h);
    Hecho findById(String id);
    List<Hecho> allHechos();
    void delete(Hecho h);

    // NUEVO
    void deleteAll();
    int count();

    // Consultas específicas
    List<Hecho> findByColeccionId(String coleccionId);
    List<Hecho> findByCategoria(CategoriaHechoEnum categoria);
    List<Hecho> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    List<Hecho> findByTituloContainingIgnoreCase(String q);

}
