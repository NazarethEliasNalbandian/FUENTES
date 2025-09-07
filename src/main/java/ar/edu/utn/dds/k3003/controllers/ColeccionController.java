package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/colecciones", produces = "application/json")
public class ColeccionController {

    private final FachadaFuente fachadaFuente;

    @Autowired
    public ColeccionController(FachadaFuente fachadaFuente){
        this.fachadaFuente = fachadaFuente;
    }

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> listarColecciones() {
        // Si hay error interno, que lo capture un @ControllerAdvice (recomendado)
        List<ColeccionDTO> retorno = fachadaFuente.colecciones();
        return ResponseEntity.ok(retorno);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<ColeccionDTO> obtenerColeccion(@PathVariable String nombre) {
        ColeccionDTO retorno = fachadaFuente.buscarColeccionXId(nombre);
        if (Objects.isNull(retorno)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(retorno);
    }

    @GetMapping("/{nombre}/hechos")
    public ResponseEntity<List<?>> obtenerHechosColeccion(@PathVariable String nombre) {
        ColeccionDTO coleccionDTO = fachadaFuente.buscarColeccionXId(nombre);
        if (Objects.isNull(coleccionDTO)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(fachadaFuente.buscarHechosXColeccion(nombre));
    }

    // Recibir un DTO en vez de la entidad JPA
    @PostMapping(consumes = "application/json")
    public ResponseEntity<ColeccionDTO> crearColeccion(@RequestBody @Valid ColeccionDTO coleccionDTO) {
        ColeccionDTO creada = fachadaFuente.agregar(coleccionDTO);
        // 201 Created + Location
        return ResponseEntity
                .created(URI.create("/api/colecciones/" + creada.nombre()))
                .body(creada);
    }
}
