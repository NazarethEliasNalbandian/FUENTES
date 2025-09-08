package ar.edu.utn.dds.k3003.controllers;
/*ResponseEntity<String> entity = template.getForEntity("https://example.com", String.class);
 String body = entity.getBody();
 MediaType contentType = entity.getHeaders().getContentType();
 HttpStatus statusCode = entity.getStatusCode();*/

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoEstadoRequestDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import ar.edu.utn.dds.k3003.model.Hecho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/hecho")
public class HechoController {
    private final FachadaFuente fachadaFuente;
    @Autowired
    public HechoController(FachadaFuente fachadaFuente){
        this.fachadaFuente = fachadaFuente;
    }

/*    @PostMapping
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody Hecho hecho) {
        HechoDTO hechoDTO = new HechoDTO(hecho.getId(),hecho.getNombreColeccion(),hecho.getTitulo(),hecho.getEtiquetas(),hecho.getCategoria(),hecho.getUbicacion(),hecho.getFecha(),hecho.getOrigen());
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(hechoDTO));
        } catch (Exception e){
            return new ResponseEntity<>(new HechoDTO("null"," null","null"),HttpStatus.BAD_REQUEST);
        }
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<HechoDTO> obtenerHecho(@PathVariable String id) {
        HechoDTO retorno = fachadaFuente.buscarHechoXId(id);
        if(Objects.isNull(retorno)){
            return new ResponseEntity<>(new HechoDTO("null","null","null"),HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(retorno);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HechoDTO> corregirEstado(@PathVariable String id,
                                                   @RequestBody HechoEstadoRequestDTO body) {
        try {
            HechoDTO hecho = fachadaFuente.buscarHechoXId(id);
            EstadoHechoEnum nuevoEstado = EstadoHechoEnum.valueOf(body.estado().toUpperCase());

            HechoDTO actualizado = fachadaFuente.actualizarEstado(id, nuevoEstado);

            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> borrarTodos() {
        int eliminados = fachadaFuente.borrarTodosLosHechos();
        Map<String, Object> body = new HashMap<>();
        body.put("eliminados", eliminados);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<HechoDTO>> listarActivos() {
        List<HechoDTO> activos = fachadaFuente.hechosActivos();
        if (activos.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay
        }
        return ResponseEntity.ok(activos); // 200 con la lista
    }

    @PostMapping("/{id}/pdis")
    public ResponseEntity<PdIDTO> agregarPdiAHecho(@PathVariable String id, @RequestBody PdIDTO body) {
        try {
            var etiquetasSeguras = (body.etiquetas() == null) ? List.<String>of() : body.etiquetas();
            PdIDTO pdi = new PdIDTO(
                    null,                       // id lo genera Procesador
                    id,                         // hechoId desde la URL (no dependemos del body)
                    body.descripcion(),
                    body.lugar(),
                    body.momento(),             // asegurate que en Postman sea "2025-10-09T14:00:00"
                    body.contenido(),
                    etiquetasSeguras
            );
            PdIDTO result = fachadaFuente.agregar(pdi);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422).build();
        }
    }

}

