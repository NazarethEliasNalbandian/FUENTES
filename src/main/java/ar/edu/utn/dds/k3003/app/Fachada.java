package ar.edu.utn.dds.k3003.app;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import ar.edu.utn.dds.k3003.model.Hecho;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.repositories.real.ColRepository;
import ar.edu.utn.dds.k3003.repositories.real.HecRepository;
import ar.edu.utn.dds.k3003.repositories.PdiRepository;
import ar.edu.utn.dds.k3003.repositories.ColeccionMapper;
import ar.edu.utn.dds.k3003.repositories.HechoMapper;
import ar.edu.utn.dds.k3003.repositories.PdiMapper;

@Service
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaFuente {

    private ColRepository coleccionRepository;   // <- interfaz real
    private HecRepository hechoRepository;       // <- interfaz real
    private PdiRepository pdiRepository;         // <- siempre en memoria
    private ColeccionMapper coleccionMapper;
    private HechoMapper hechoMapper;
    private PdiMapper pdiMapper;

    @Autowired
    private FachadaProcesadorPdI fachadaprocesadorPdI;

    // Constructor vacío (opcional; no inicializa nada)
    Fachada() {
    }

    // Inyección por constructor con las interfaces reales
    @Autowired
    public Fachada(
            ColRepository coleccionRepository,
            HecRepository hechoRepository,
            PdiRepository pdiRepository,
            ColeccionMapper coleccionMapper,
            HechoMapper hechoMapper,
            PdiMapper pdiMapper
    ) {
        this.coleccionRepository = coleccionRepository;
        this.hechoRepository = hechoRepository;
        this.pdiRepository = pdiRepository;
        this.coleccionMapper = coleccionMapper;
        this.hechoMapper = hechoMapper;
        this.pdiMapper = pdiMapper;
    }

    public HecRepository getHechoRepository() {
        return this.hechoRepository;
    }

    public HechoMapper getHechoMapper() {
        return this.hechoMapper;
    }

    public ColRepository getColeccionRepository() {
        return this.coleccionRepository;
    }

    public ColeccionMapper getColeccionMapper() {
        return this.coleccionMapper;
    }

    public PdiRepository getPdiRepository() {
        return this.pdiRepository;
    }

    public PdiMapper getPdiMapper() {
        return this.pdiMapper;
    }

    @Override
    public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
        ColeccionDTO comparable = this.buscarColeccionXId(coleccionDTO.nombre());
        if (!Objects.isNull(comparable)) {
            throw new IllegalArgumentException("La coleccion ya fue agregada anteriormente");
        }
        System.out.println("CORRECTO AGREGADO DE COLECCION");
        return coleccionMapper.map(this.coleccionRepository.save(coleccionMapper.map(coleccionDTO)));
    }

    @Override
    public HechoDTO agregar(HechoDTO hechoDTO) {
        if (hechoDTO == null) throw new IllegalArgumentException("HechoDTO requerido");

        // normalizo strings
        String coleccion = hechoDTO.nombreColeccion() == null ? null : hechoDTO.nombreColeccion().trim();
        String id        = hechoDTO.id() == null ? null : hechoDTO.id().trim();

        if (coleccion == null || buscarColeccionXId(coleccion) == null) {
            throw new IllegalArgumentException("No hay una coleccion para asociar al hecho");
        }

        HechoDTO comparable = (id == null) ? null : this.buscarHechoXId(id);
        if (comparable != null) {
            if (Objects.equals(comparable.nombreColeccion(), hechoDTO.nombreColeccion())
                    && Objects.equals(comparable.ubicacion(), hechoDTO.ubicacion())
                    && Objects.equals(comparable.titulo(), hechoDTO.titulo())
                    && Objects.equals(comparable.categoria(), hechoDTO.categoria())
                    && Objects.equals(comparable.etiquetas(), hechoDTO.etiquetas())
                    && Objects.equals(comparable.origen(), hechoDTO.origen())
                    && Objects.equals(comparable.fecha(), hechoDTO.fecha())) {
                throw new IllegalArgumentException("El hecho ya fue agregado anteriormente");
            } else {
                this.hechoRepository.delete(this.hechoRepository.findById(comparable.id()));
            }
        }

        return hechoMapper.map(this.hechoRepository.save(hechoMapper.map(hechoDTO)));
    }


    @Override
    public PdIDTO agregar(PdIDTO pdiDTO) {
        try {
            PdIDTO pedidoProcesador = fachadaprocesadorPdI.procesar(pdiDTO);
            pdiDTO = pedidoProcesador;
        } catch (Exception e) {
            System.out.println("No funciono la conexion");
            throw new IllegalStateException("Ha resultado invalido el procesamiento de la PDI");
        }
        if (pdiDTO == null) {
            throw new IllegalStateException();
        }

        if (Objects.isNull(buscarHechoXId(pdiDTO.hechoId()))) {
            return null;
        }
        System.out.println("CORRECTO AGREGADO DE PDI");
        return pdiMapper.map(this.pdiRepository.save(pdiMapper.map(pdiDTO)));
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String idColeccion) {
        System.out.println("CORRECTA BUSQUEDA DE COLECCION POR ID");
        return coleccionMapper.map(coleccionRepository.findById(idColeccion));
    }

    @Override
    public HechoDTO buscarHechoXId(String idHecho) {
        System.out.println("CORRECTA BUSQUEDA DE HECHO POR ID");
        return hechoMapper.map(hechoRepository.findById(idHecho));
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String coleccionId) {
        System.out.println("CORRECTA DEVOLUCION DE LISTA DE HECHOS POR COLECCION");
        return hechoRepository.allHechos().stream()
                .filter(hecho -> hecho.getNombreColeccion().equals(coleccionId))
                .map(hechoMapper::map)
                .toList();
    }

    @Override
    public void setProcesadorPdI(FachadaProcesadorPdI fachadaProcesadorPdI) {
        this.fachadaprocesadorPdI = fachadaProcesadorPdI;
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        List<ColeccionDTO> retorno = coleccionRepository.allColeccciones().stream()
                .map(coleccionMapper::map)
                .toList();
        if (Objects.isNull(retorno)) {
            retorno = new ArrayList<>();
        }
        System.out.println("CORRECTa devolucion DE COLECCIONES");
        return retorno;
    }


    @Override
    @Transactional
    public int borrarTodosLosHechos() {
        int cantidad = this.hechoRepository.count();
        this.hechoRepository.deleteAll();
        return cantidad;
    }

    @Override
    @Transactional
    public int borrarTodasLasColecciones() {
        // Primero borro todos los hechos (opcional)
        this.hechoRepository.deleteAll();
        int cantidad = this.coleccionRepository.count();
        this.coleccionRepository.deleteAll();
        return cantidad;
    }

    @Override
    @Transactional
    public HechoDTO actualizarEstado(String hechoId, EstadoHechoEnum nuevoEstado) {
        if (hechoId == null || hechoId.isBlank()) {
            throw new IllegalArgumentException("hechoId requerido");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("nuevoEstado requerido");
        }

        Hecho h = this.hechoRepository.findById(hechoId);
        if (h == null) {
            throw new NoSuchElementException("Hecho no encontrado: " + hechoId);
        }

        h.setEstado(nuevoEstado);
        Hecho guardado = this.hechoRepository.save(h); // upsert en tu repo

        return this.hechoMapper.map(guardado);
    }

}
