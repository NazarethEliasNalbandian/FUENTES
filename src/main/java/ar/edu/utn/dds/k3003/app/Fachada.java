package ar.edu.utn.dds.k3003.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.repositories.*;

@Service
public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaFuente {
    //private FuenteMapper fuenteMapper; Siento que debe ser necesario, pero no le encuentro realmente utilidad.
    //private FuenteRepository fuenteRepository;
    private ColeccionRepository coleccionRepository;
    private HechoRepository hechoRepository;
    private PdiRepository pdiRepository;
    private ColeccionMapper coleccionMapper;
    private HechoMapper hechoMapper;
    private PdiMapper pdiMapper;
    @Autowired
    private FachadaProcesadorPdI fachadaprocesadorPdI;

    Fachada(){
    //    this.fuenteMapper = new FuenteMapper();
    //    this.fuenteRepository = new FuenteRepository();
        this.hechoRepository = new HechoRepository();
        this.coleccionRepository = new ColeccionRepository();
        this.pdiRepository = new PdiRepository();
        this.hechoMapper = new HechoMapper();
        this.coleccionMapper = new ColeccionMapper();
        this.pdiMapper = new PdiMapper();
    }

    public HechoRepository getHechoRepository(){
        return this.hechoRepository;
    }
    public HechoMapper getHechoMapper(){
        return this.hechoMapper;
    }
    public ColeccionRepository getColeccionRepository(){
        return this.coleccionRepository;
    }
    public ColeccionMapper getColeccionMapper(){
        return this.coleccionMapper;
    }
    public PdiRepository getPdiRepository(){
        return this.pdiRepository;
    }
    public PdiMapper getPdiMapper(){
        return this.pdiMapper;
    }

    @Override
    public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
        ColeccionDTO comparable = this.buscarColeccionXId(coleccionDTO.nombre());
        if(!Objects.isNull(comparable)){
            throw new IllegalArgumentException("La coleccion ya fue agregada anteriormente");
        }
        System.out.println("CORRECTO AGREGADO DE COLECCION");
        return coleccionMapper.map(this.coleccionRepository.save(coleccionMapper.map(coleccionDTO)));   
    }

    @Override
    public HechoDTO agregar(HechoDTO hechoDTO) {
        if(Objects.isNull(buscarColeccionXId(hechoDTO.nombreColeccion()))){
            throw new IllegalArgumentException("No hay una coleccion para asociar al hecho");
        }
        HechoDTO comparable = this.buscarHechoXId(hechoDTO.id());
        if(!Objects.isNull(comparable)){
            if(Objects.equals(comparable.nombreColeccion(), hechoDTO.nombreColeccion()) && Objects.equals(comparable.ubicacion(), hechoDTO.ubicacion()) && Objects.equals(comparable.titulo(),hechoDTO.titulo()) && Objects.equals(comparable.categoria(), hechoDTO.categoria()) && Objects.equals(comparable.etiquetas(), hechoDTO.etiquetas()) && Objects.equals(comparable.origen(), hechoDTO.origen()) && Objects.equals(comparable.fecha(), hechoDTO.fecha())){
                System.out.println("ES DECIR, LLEGAMOS CON COLECCION: "+comparable.nombreColeccion() + "ES DECIR, LLEGAMOS CON UBICACION: "+comparable.ubicacion() + "ES DECIR, LLEGAMOS CON TITULO: "+comparable.titulo() +"ES DECIR, LLEGAMOS CON CATEGORIA: "+comparable.categoria() + "ES DECIR, LLEGAMOS CON ETIQUETAS: "+comparable.etiquetas() + "ES DECIR, LLEGAMOS CON ORIGEN: "+comparable.origen() +"ES DECIR, LLEGAMOS CON FECHA: "+comparable.fecha());
                throw new IllegalArgumentException("El hecho ya fue agregado anteriormente");        
            } else {
                this.hechoRepository.delete(this.hechoRepository.findById(comparable.id()));
            }
        } 
        System.out.println("CORRECTO AGREGADO DE HECHO");
        return hechoMapper.map(this.hechoRepository.save(hechoMapper.map(hechoDTO)));// SE RETORNA EL HECHODTO QUE NOS DIERON
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
        if(pdiDTO == null){
            throw new IllegalStateException();
        }
        
        if(Objects.isNull(buscarHechoXId(pdiDTO.hechoId()))){
            return null;
        }
        System.out.println("CORRECTO AGREGADO DE PDI");
        return pdiMapper.map( this.pdiRepository.save(pdiMapper.map(pdiDTO)));
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String idColeccion) {
        System.out.println("CORRECTA BUSQUEDA DE COLECCION POR ID");
        return coleccionMapper.map(coleccionRepository.findById(idColeccion));
    }

    @Override
    public HechoDTO buscarHechoXId(String idHecho){
        System.out.println("CORRECTA BUSQUEDA DE HECHO POR ID");
        return  hechoMapper.map(hechoRepository.findById(idHecho));
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String coleccionId){  
        System.out.println("CORRECTA DEVOLUCION DE LISTA DE HECHOS POR COLECCION");  
        return hechoRepository.allHechos().stream().filter(hecho->hecho.getNombreColeccion().equals(coleccionId)).map(hechoMapper::map).toList();
    }

    @Override
    public void setProcesadorPdI(FachadaProcesadorPdI fachadaProcesadorPdI) {
        this.fachadaprocesadorPdI = fachadaProcesadorPdI;
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        //no tengo idea que deberia hacer, asi que envio todas las colecciones
        List<ColeccionDTO> retorno =coleccionRepository.allColeccciones().stream().map(coleccionMapper::map).toList();
        if(Objects.isNull(retorno)){
            retorno = new ArrayList<>();
        }
        System.out.println("CORRECTa devolucion DE COLECCIONES");
        return retorno;
    }    
}
