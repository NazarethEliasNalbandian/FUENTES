package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;
import ar.edu.utn.dds.k3003.repositories.jpa.SpringDataHechoJpa;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("!test")
public class JpaHechoRepository implements HecRepository {

    private final SpringDataHechoJpa jpa;

    public JpaHechoRepository(SpringDataHechoJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Hecho save(Hecho h) {
        return jpa.save(h);
    }

    @Override
    public Hecho findById(String id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    public List<Hecho> allHechos() {
        return jpa.findAll();
    }

    @Override
    public void delete(Hecho h) {
        jpa.delete(h);
    }

    @Override
    public List<Hecho> findByColeccionId(String coleccionId) {
        // Delego al nombre de propiedad real en la entidad
        return jpa.findByNombreColeccion(coleccionId);
    }

    @Override
    public List<Hecho> findByCategoria(CategoriaHechoEnum categoria) {
        return jpa.findByCategoria(categoria);
    }

    @Override
    public List<Hecho> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta) {
        return jpa.findByFechaBetween(desde, hasta);
    }

    @Override
    public List<Hecho> findByTituloContainingIgnoreCase(String q) {
        return jpa.findByTituloContainingIgnoreCase(q);
    }

    // NUEVO
    @Override public void deleteAll() { jpa.deleteAll(); }
    @Override public int count() { return (int) jpa.count(); }
}
