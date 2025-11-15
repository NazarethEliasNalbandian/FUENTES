package ar.edu.utn.dds.k3003.app;

import java.util.*;

import ar.edu.utn.dds.k3003.WorkerHandler;
import ar.edu.utn.dds.k3003.clients.SolicitudesClient;
import ar.edu.utn.dds.k3003.clients.dto.ProcesamientoResponseDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.HechoMongo;
import ar.edu.utn.dds.k3003.model.SolicitudMongo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoFiltroDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.repositories.real.ColRepository;
import ar.edu.utn.dds.k3003.repositories.real.HecRepository;
import ar.edu.utn.dds.k3003.repositories.PdiRepository;
import ar.edu.utn.dds.k3003.repositories.ColeccionMapper;
import ar.edu.utn.dds.k3003.repositories.HechoMapper;
import ar.edu.utn.dds.k3003.repositories.HechoMongoSearchRepositoryImpl;
import ar.edu.utn.dds.k3003.repositories.HechoMongoMapper;
import ar.edu.utn.dds.k3003.repositories.PdiMapper;
import ar.edu.utn.dds.k3003.repositories.PdiMongoMapper;

@Service
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaFuente {

    private ColRepository coleccionRepository;   // <- interfaz real
    private HecRepository hechoRepository;       // <- interfaz real
    private PdiRepository pdiRepository;         // <- siempre en memoria
    private ColeccionMapper coleccionMapper;
    private HechoMapper hechoMapper;
    private PdiMapper pdiMapper;

    @Autowired
    private SolicitudesClient solicitudesClient;

    @Autowired
    private FachadaProcesadorPdI fachadaprocesadorPdI;
    
    @Autowired
    private WorkerHandler worker; 
    // Constructor vacío (opcional; no inicializa nada)
    @Autowired
    private HechoMongoSearchRepositoryImpl repositoryMongo;
    @Autowired
    private HechoMongoMapper mongoMapper;
    @Autowired
    private PdiMongoMapper mongoMapperPdi;
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
    @Autowired
    public void setWorker(WorkerHandler worker) {
        this.worker = worker;
        this.worker.setFachada(this);
    }
    public void hacerAlgo(String string){
        System.out.println(string);
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
        this.repositoryMongo.guardarDesdeDTO(this.mongoMapper.mapDTO(hechoDTO));
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
        HechoMongo hechoEncontrado = this.repositoryMongo.buscarPorId(hechoId);
        if (hechoEncontrado != null) {
            hechoEncontrado.setEstado(nuevoEstado);
            hechoEncontrado.setSolicitudes(this.solicitudesClient.findAllByHecho(hechoId).stream().map(dto -> new SolicitudMongo(dto.descripcion(), dto.estado())).toList());
            this.repositoryMongo.guardarDesdeDTO(hechoEncontrado);
            // upsert en Mongo
        }

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

        HechoMongo hechoEncontrado = this.repositoryMongo.buscarPorId(hecho.getId());
        if (hechoEncontrado != null) {
            hechoEncontrado.setEtiquetas(hecho.getEtiquetas());
            List<PdIDTO> pdis_hecho = this.fachadaprocesadorPdI.buscarPorHecho(hechoEncontrado.getId());
            hechoEncontrado.setPdiIds(pdis_hecho.stream().map(mongoMapperPdi::mapDTO).toList());
            this.repositoryMongo.guardarDesdeDTO(hechoEncontrado);  // upsert en Mongo
        }

        String pdiId = proc.pdiId();
        if (pdiId != null && !pdiId.isBlank() && !hecho.getPdiIds().contains(pdiId)) {
            hecho.getPdiIds().add(pdiId);
        }

        this.hechoRepository.save(hecho);

        // 4) Devolver el resultado al caller (controller)
        return proc;
    }

    @Override
    public List<HechoMongo> buscarMongoTodos(){
        return this.repositoryMongo.buscarTodos();
    }
    public List<HechoDTO> buscarHechosSinSolicitudes() {
        return hechoRepository.allHechos()
                .stream()
                .filter(h -> solicitudesClient.findByHecho(h.getId()).isEmpty())
                .map(hechoMapper::map)
                .toList();
    }

    public ProcesamientoResponseDTO aplicarProcesamiento(String hechoId, ProcesamientoResponseDTO proc) {

        var hecho = hechoRepository.findById(hechoId);

        // --- 3) Actualizar etiquetas ---
        if (hecho.getEtiquetas() == null) hecho.setEtiquetas(new ArrayList<>());

        LinkedHashSet<String> union = new LinkedHashSet<>(hecho.getEtiquetas());
        if (proc.etiquetas() != null) union.addAll(proc.etiquetas());
        hecho.setEtiquetas(new ArrayList<>(union));

        // --- 4) Actualizar pdiIds ---
        if (hecho.getPdiIds() == null) hecho.setPdiIds(new ArrayList<>());

        if (proc.pdiId() != null &&
                !proc.pdiId().isBlank() &&
                !hecho.getPdiIds().contains(proc.pdiId())) {
            hecho.getPdiIds().add(proc.pdiId());
        }

        // Persistir en relacional
        hechoRepository.save(hecho);

        // Persistir en Mongo
        HechoMongo hechoMongo = repositoryMongo.buscarPorId(hecho.getId());
        if (hechoMongo != null) {

            System.out.println("--------------------------------------");
            System.out.println("📌 MONGO → HechoMongo ANTES de actualizar:");
            System.out.println("   Etiquetas Mongo DESPUÉS: " + hechoMongo.getEtiquetas());
            System.out.println("   PdiIds Mongo DESPUÉS   : " + hechoMongo.getPdiIds());

            hechoMongo.setEtiquetas(hecho.getEtiquetas());

            System.out.println("--------------------------------------");
            System.out.println("📌 MONGO → HechoMongo DESPUÉS de actualizar:");
            System.out.println("   Etiquetas Mongo DESPUÉS: " + hechoMongo.getEtiquetas());
            System.out.println("   PdiIds Mongo DESPUÉS   : " + hechoMongo.getPdiIds());

/*          List<PdIDTO> pdis = this.fachadaprocesadorPdI.buscarPorHecho(hechoMongo.getId());
            hechoMongo.setPdiIds(pdis.stream().map(mongoMapperPdi::mapDTO).toList());*/

            repositoryMongo.guardarDesdeDTO(hechoMongo);
        }

        return proc;
    }

    @Override
    public List<HechoDTO> buscarHechosFiltrados(Map<String,String> filtros){
        String key;
        //Boolean pdi_contenido = false;
        //Boolean pdi_etiquetas = false;
        //Boolean pdi_descripcion = false;
        //Boolean pdi_lugar = false;
        HechoFiltroDTO filtros_mongo = new HechoFiltroDTO(null,null,null,null,null,null,null,null,null,null,null);
        for(Map.Entry<String, String> filtro : filtros.entrySet()){
            key = filtro.getKey().toLowerCase();
            String value = filtro.getValue();
            switch(key){
                case "titulo":
                    filtros_mongo.setTitulo(value);
                    break;
                case "etiquetas":
                    filtros_mongo.setEtiquetas(value);
                    break;
                case "origen":
                    filtros_mongo.setOrigen(value);
                    break;
                case "ubicacion": 
                    filtros_mongo.setUbicacion(value);
                    break;
                case "estado":
                    filtros_mongo.setEstado(value);
                    break;
                case "categoria":
                    filtros_mongo.setCategoria(value);
                    break;
                case "pdi_contenido": 
                    //pdi_contenido = true;
                    filtros_mongo.setPdi_contenido(value);
                    break;
                case "pdi_descripcion":
                    //pdi_descripcion = true;
                    filtros_mongo.setPdi_descripcion(value);
                    break;
                case "pdi_lugar":
                    //pdi_lugar=true;
                    filtros_mongo.setPdi_lugar(value);
                    break;
                case "pdi_etiquetas":
                    //pdi_etiquetas = true;
                    filtros_mongo.setPdi_etiquetas(value);
                    break;
                default:
                    System.out.println("Filtro no reconocido"); 
                    break;
            }
        }

        List<HechoDTO> hechos = this.repositoryMongo.buscarConFiltros(filtros_mongo).stream().map(mongoMapper::mapDTO).toList();
        // if (pdi_etiquetas || pdi_contenido || pdi_lugar || pdi_descripcion) {
        //     List<HechoDTO> hechosFiltrados = new ArrayList<>();
        //     for (HechoDTO hecho : hechos) {
        //         List<PdIDTO> pdis = this.fachadaprocesadorPdI.buscarPorHecho(hecho.id());
        //         boolean cumple = true;
        //         if (pdi_contenido) {
        //             cumple &= pdis.stream()
        //             .anyMatch(p->p.contenido().toLowerCase().contains(filtros_mongo.getPdi_contenido()));
        //         }
        //         if (pdi_lugar) {
        //             cumple &= pdis.stream()
        //             .anyMatch(p -> p.lugar().toLowerCase().contains(filtros_mongo.getPdi_lugar()));
        //         }
        //         if (pdi_descripcion) {
        //             cumple &= pdis.stream()
        //             .anyMatch(p -> p.descripcion().toLowerCase().contains(filtros_mongo.getPdi_descripcion()));
        //         }
        //         if (pdi_etiquetas) {
        //             cumple &= pdis.stream()
        //             .anyMatch(p -> p.etiquetas().stream()
        //             .anyMatch(e->e.toLowerCase().contains(filtros_mongo.getPdi_etiquetas())));
        //         }
        //         if (cumple) {
        //             hechosFiltrados.add(hecho);
        //         }
        //     }
        //     hechos = hechosFiltrados;
        // }
        return hechos;
    }
}
