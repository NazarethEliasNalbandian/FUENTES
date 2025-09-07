package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HechoRepository extends JpaRepository<Hecho, String> {
    List<Hecho> findByNombreColeccion(String nombreColeccion);
    boolean existsById(String id);
}
