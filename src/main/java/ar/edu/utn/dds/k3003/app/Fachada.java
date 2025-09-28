package ar.edu.utn.dds.k3003.app;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import ar.edu.utn.dds.k3003.clients.dto.ProcesamientoResponseDTO;
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

    @Override
    public List<HechoDTO> hechosActivos() {
        return this.hechoRepository.allHechos().stream()
                .filter(h -> EstadoHechoEnum.ACTIVO.equals(h.getEstado()))
                .map(this.hechoMapper::map)
                .toList();
    }

    // en ar.edu.utn.dds.k3003.app.Fachada
    @Override
    @jakarta.transaction.Transactional
    public ProcesamientoResponseDTO agregar(PdIDTO pdiDTO) throws IllegalStateException {
        if (pdiDTO == null) throw new IllegalArgumentException("PdIDTO requerido");
        if (pdiDTO.hechoId() == null || pdiDTO.hechoId().isBlank())
            throw new IllegalArgumentException("hechoId requerido en PdIDTO");

        Hecho hecho = this.hechoRepository.findById(pdiDTO.hechoId());
        if (hecho == null)
            throw new NoSuchElementException("Hecho no encontrado: " + pdiDTO.hechoId());

        // 1) Procesar en ProcesadorPdI y recibir el resumen
        final ProcesamientoResponseDTO proc;
        try {
            proc = fachadaprocesadorPdI.procesar(pdiDTO);
        } catch (Exception e) {
            throw new IllegalStateException("Ha resultado inválido el procesamiento de la PdI", e);
        }
        if (proc == null) throw new IllegalStateException("ProcesadorPdI devolvió nulo");

        // 2) Si NO se procesó, no persistimos nada y devolvemos tal cual
        if (!"PROCESSED".equalsIgnoreCase(proc.estado())) {
            return proc;
        }


        // 3) Actualizar Hecho: unir etiquetas y agregar pdiId (sin duplicar) y persistir
        if (hecho.getEtiquetas() == null) hecho.setEtiquetas(new java.util.ArrayList<>());
        if (hecho.getPdiIds() == null) hecho.setPdiIds(new java.util.ArrayList<>());

        java.util.LinkedHashSet<String> union = new java.util.LinkedHashSet<>(hecho.getEtiquetas());
        if (proc.etiquetas() != null) union.addAll(proc.etiquetas());
        hecho.setEtiquetas(new java.util.ArrayList<>(union));

        String pdiId = proc.pdiId();
        if (pdiId != null && !pdiId.isBlank() && !hecho.getPdiIds().contains(pdiId)) {
            hecho.getPdiIds().add(pdiId);
        }

        this.hechoRepository.save(hecho);

        // 4) Devolver el resultado al caller (controller)
        return proc;
    }



}
