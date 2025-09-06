package ar.edu.utn.dds.k3003.controllers;


import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.Coleccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionController {
    private final FachadaFuente fachadaFuente;
    @Autowired
    public ColeccionController(FachadaFuente fachadaFuente){
        this.fachadaFuente = fachadaFuente;
    }   
    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> listarColecciones() {
        List<ColeccionDTO> retorno;
        try {
            retorno = fachadaFuente.colecciones();
        } catch (Exception e){
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.BAD_GATEWAY);
        }
        return ResponseEntity.ok(retorno);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ColeccionDTO> obtenerColeccion(@PathVariable String nombre) {
        ColeccionDTO retorno = fachadaFuente.buscarColeccionXId(nombre);
        if(Objects.isNull(retorno)){
            return new ResponseEntity<>(new ColeccionDTO("null","null"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(retorno);
    }

    @GetMapping("/{nombre}/hechos")
    public ResponseEntity<List<HechoDTO>> obtenerHechosColeccion(@PathVariable String nombre) {
        ColeccionDTO coleccionDTO = fachadaFuente.buscarColeccionXId(nombre);
        if(Objects.isNull(coleccionDTO)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST); 
        }
        List<HechoDTO> retorno = fachadaFuente.buscarHechosXColeccion(nombre);
        return ResponseEntity.ok(retorno);
    }

    @PostMapping
    public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody Coleccion coleccion) {
        ColeccionDTO coleccionDTO = new ColeccionDTO(coleccion.getNombre(), coleccion.getDescripcion());
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(coleccionDTO));
        } catch (Exception e){
            return new ResponseEntity<>(new ColeccionDTO("null"," null"),HttpStatus.BAD_REQUEST);
        }
    }
}

