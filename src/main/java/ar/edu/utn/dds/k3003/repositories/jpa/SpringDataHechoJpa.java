package ar.edu.utn.dds.k3003.repositories.jpa;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("!test")
public interface SpringDataHechoJpa extends JpaRepository<Hecho, String> {

    List<Hecho> findByNombreColeccion(String nombreColeccion);    List<Hecho> findByCategoria(CategoriaHechoEnum categoria);
    List<Hecho> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    List<Hecho> findByTituloContainingIgnoreCase(String q);
}
