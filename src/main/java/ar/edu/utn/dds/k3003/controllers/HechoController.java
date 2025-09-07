package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/hechos", produces = "application/json")
public class HechoController {

    private final FachadaFuente fachadaFuente;

    @Autowired
    public HechoController(FachadaFuente fachadaFuente){
        this.fachadaFuente = fachadaFuente;
    }

    // Mejor recibir DTO directamente
    @PostMapping(consumes = "application/json")
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody @Valid HechoDTO hechoDTO) {
        HechoDTO creado = fachadaFuente.agregar(hechoDTO);
        return ResponseEntity
                .created(URI.create("/api/hechos/" + creado.id()))
                .body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HechoDTO> obtenerHecho(@PathVariable String id) {
        HechoDTO retorno = fachadaFuente.buscarHechoXId(id);
        if (Objects.isNull(retorno)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(retorno);
    }

    // PATCH semántico: solo etiquetas (ejemplo)
    public static record HechoPatchEtiquetasRequest(List<String> etiquetas) {}

    @PatchMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<HechoDTO> patchEtiquetas(@PathVariable String id,
                                                   @RequestBody @Valid HechoPatchEtiquetasRequest body) {
        HechoDTO actual = fachadaFuente.buscarHechoXId(id);
        if (Objects.isNull(actual)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // Merge sin duplicados preservando orden
        List<String> merged = new ArrayList<>(new LinkedHashSet<>(actual.etiquetas() == null ? List.of() : actual.etiquetas()));
        if (body.etiquetas() != null) {
            merged.addAll(body.etiquetas());
            merged = new ArrayList<>(new LinkedHashSet<>(merged));
        }

        // Reutilizamos agregar(...) como upsert sobre el mismo id (tu Fachada borra/reinserta si cambia algo).
        HechoDTO actualizado = fachadaFuente.agregar(new HechoDTO(
                id,
                actual.nombreColeccion(),
                actual.titulo(),
                merged,
                actual.categoria(),
                actual.ubicacion(),
                actual.fecha(),
                actual.origen()
        ));
        return ResponseEntity.ok(actualizado);
    }
}
