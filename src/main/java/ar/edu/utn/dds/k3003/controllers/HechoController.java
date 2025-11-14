package ar.edu.utn.dds.k3003.controllers;
/*ResponseEntity<String> entity = template.getForEntity("https://example.com", String.class);
 String body = entity.getBody();
 MediaType contentType = entity.getHeaders().getContentType();
 HttpStatus statusCode = entity.getStatusCode();*/

import ar.edu.utn.dds.k3003.clients.dto.ProcesamientoResponseDTO;
import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoEstadoRequestDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.EstadoHechoEnum;
import ar.edu.utn.dds.k3003.model.HechoMongo;

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
            @SuppressWarnings("unused")
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


    @PatchMapping("/{id}/procesamiento")
    public ResponseEntity<?> aplicarProcesamiento(
            @PathVariable String id,
            @RequestBody ProcesamientoResponseDTO body) {

        try {
            // 1) Buscar el hecho
            HechoDTO hecho = fachadaFuente.buscarHechoXId(id);
            if (hecho == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Hecho no encontrado");
            }

            // 2) Aplicar procesamiento por fachada
            //    (Esto internamente hace: unir etiquetas + actualizar pdis + persistir en ambas DBs)
            ProcesamientoResponseDTO actualizado = fachadaFuente.aplicarProcesamiento(id, body);

            return ResponseEntity.ok(actualizado);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Hecho inexistente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Datos inválidos");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
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
    @GetMapping("/busquedamongo")
    public ResponseEntity<List<HechoMongo>> listarMongo(){
        List<HechoMongo> hechosMongo = fachadaFuente.buscarMongoTodos();
        if (hechosMongo.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay
        }
        return ResponseEntity.ok(hechosMongo); 
    }
    @PostMapping("/{id}/pdis")
    public ResponseEntity<ProcesamientoResponseDTO> agregarPdiAHecho(   
            @PathVariable String id,
            @RequestBody PdIDTO body) {
        try {
            // El hechoId SIEMPRE viene de la URL
            PdIDTO pdi = new PdIDTO(
                    null,
                    id,
                    body.descripcion(),
                    body.lugar(),
                    body.momento(),
                    body.contenido(),
                    body.imageUrl(),   // <<< importante
                    List.of()
            );

            // Delegamos el flujo completo (procesar + actualizar Hecho si corresponde)
            ProcesamientoResponseDTO res = fachadaFuente.agregar(pdi);

            // Si procesó, 201; si no, 200 con procesada=false
            return ResponseEntity
                    .status("PROCESSED".equalsIgnoreCase(res.estado()) ? HttpStatus.CREATED : HttpStatus.OK)
                    .body(res);

        } catch (NoSuchElementException e) {
            // No existe el Hecho (o la Colección del Hecho)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProcesamientoResponseDTO(null, "ERROR", List.of()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ProcesamientoResponseDTO(null, "ERROR", List.of()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(422)
                    .body(new ProcesamientoResponseDTO(null, "ERROR", List.of()));
        }
    }

    @GetMapping("/sin_solicitudes")
    public ResponseEntity<List<HechoDTO>> buscarHechosSinSolicitudes() {
        var res = fachadaFuente.buscarHechosSinSolicitudes();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/busqueda")
    public ResponseEntity<?> buscar(@RequestParam Map<String, String> filtros) {
        System.out.println(filtros);
        for (Map.Entry<String,String> entry : filtros.entrySet()){
            String clave  = entry.getKey();
            String valor = entry.getValue();
            System.out.println( "Nuestro clave-valor es: " + clave + valor);
        }
        List<HechoDTO> activos = fachadaFuente.buscarHechosFiltrados(filtros);
        if (activos.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 si no hay
        }
        return ResponseEntity.ok(activos); // 200 con la lista
    }
}

