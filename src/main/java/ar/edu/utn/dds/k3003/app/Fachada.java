package ar.edu.utn.dds.k3003.app;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.repositories.*;

@Service
@Transactional
public class Fachada implements FachadaFuente {

    private final ColeccionRepository coleccionRepository;
    private final HechoRepository hechoRepository;
    private final PdiRepository pdiRepository;
    private final ColeccionMapper coleccionMapper;
    private final HechoMapper hechoMapper;
    private final PdiMapper pdiMapper;
    private final FachadaProcesadorPdI fachadaprocesadorPdI;

    @Autowired
    public Fachada(ColeccionRepository coleccionRepository,
                   HechoRepository hechoRepository,
                   PdiRepository pdiRepository,
                   ColeccionMapper coleccionMapper,
                   HechoMapper hechoMapper,
                   PdiMapper pdiMapper,
                   FachadaProcesadorPdI fachadaprocesadorPdI) {
        this.coleccionRepository = coleccionRepository;
        this.hechoRepository = hechoRepository;
        this.pdiRepository = pdiRepository;
        this.coleccionMapper = coleccionMapper;
        this.hechoMapper = hechoMapper;
        this.pdiMapper = pdiMapper;
        this.fachadaprocesadorPdI = fachadaprocesadorPdI;
    }

    @Override
    public ColeccionDTO agregar(ColeccionDTO dto) {
        if (coleccionRepository.existsByNombre(dto.nombre())) {
            throw new IllegalArgumentException("La coleccion ya existe");
        }
        var entity = coleccionMapper.map(dto);
        var saved = coleccionRepository.save(entity);
        return coleccionMapper.map(saved);
    }

    @Override
    public HechoDTO agregar(HechoDTO dto) {
        if (!coleccionRepository.existsByNombre(dto.nombreColeccion())) {
            throw new IllegalArgumentException("No existe la coleccion asociada");
        }
        var entity = hechoMapper.map(dto);
        if (entity.getId() == null || entity.getId().isBlank()) {
            entity.setId(UUID.randomUUID().toString());
        }
        var saved = hechoRepository.save(entity);
        return hechoMapper.map(saved);
    }

    @Override
    public PdIDTO agregar(PdIDTO dto) {
        var procesado = fachadaprocesadorPdI.procesar(dto);
        if (procesado == null) {
            throw new IllegalStateException("Procesador devolvió null");
        }
        if (!hechoRepository.existsById(procesado.hechoId())) {
            throw new IllegalArgumentException("No existe el hecho");
        }
        var saved = pdiRepository.save(pdiMapper.map(procesado));
        return pdiMapper.map(saved);
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String id) {
        return coleccionRepository.findById(id)
                .map(coleccionMapper::map)
                .orElse(null);
    }

    @Override
    public HechoDTO buscarHechoXId(String id) {
        return hechoRepository.findById(id)
                .map(hechoMapper::map)
                .orElse(null);
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String coleccionId) {
        return hechoRepository.findByNombreColeccion(coleccionId).stream()
                .map(hechoMapper::map)
                .toList();
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        return coleccionRepository.findAll().stream()
                .map(coleccionMapper::map)
                .toList();
    }

    @Override
    public void setProcesadorPdI(FachadaProcesadorPdI procesador) {
        // útil para tests
    }
}
