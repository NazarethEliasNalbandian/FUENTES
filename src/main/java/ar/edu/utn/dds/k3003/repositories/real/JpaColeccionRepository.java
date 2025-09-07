package ar.edu.utn.dds.k3003.repositories.real;

import ar.edu.utn.dds.k3003.model.Coleccion;
import ar.edu.utn.dds.k3003.repositories.jpa.SpringDataColeccionJpa;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// paquete: ar.edu.utn.dds.k3003.repositories
@Repository
@Profile("!test")
public class JpaColeccionRepository implements ColRepository {

    private final ar.edu.utn.dds.k3003.repositories.jpa.SpringDataColeccionJpa jpa;

    public JpaColeccionRepository(SpringDataColeccionJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Coleccion save(Coleccion c) {
        return jpa.save(c);
    }

    @Override
    public Coleccion findById(String id) {
        return jpa.findById(id).orElse(null);
    }

    @Override
    public List<Coleccion> allColeccciones() {
        return jpa.findAll();
    }

    @Override
    public void delete(Coleccion c) {
        jpa.delete(c);
    }

    @Override public void deleteAll() { jpa.deleteAll(); }
    @Override public int count() { return (int) jpa.count(); }

    // Si tu interfaz tenía estos (ajustá según tu firma real):
    @Override
    public boolean existsByNombre(String nombre) {
        return jpa.existsByNombre(nombre);
    }

    @Override
    public Optional<Coleccion> findByNombre(String nombre) {
        return jpa.findByNombre(nombre);
    }
}

