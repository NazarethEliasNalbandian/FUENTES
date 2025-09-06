package ar.edu.utn.dds.k3003.controllers;
/*ResponseEntity<String> entity = template.getForEntity("https://example.com", String.class);
 String body = entity.getBody();
 MediaType contentType = entity.getHeaders().getContentType();
 HttpStatus statusCode = entity.getStatusCode();*/

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.model.Hecho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/hecho")
public class HechoController {
    private final FachadaFuente fachadaFuente;
    @Autowired
    public HechoController(FachadaFuente fachadaFuente){
        this.fachadaFuente = fachadaFuente;
    }

    @PostMapping
    public ResponseEntity<HechoDTO> crearHecho(@RequestBody Hecho hecho) {
        HechoDTO hechoDTO = new HechoDTO(hecho.getId(),hecho.getNombreColeccion(),hecho.getTitulo(),hecho.getEtiquetas(),hecho.getCategoria(),hecho.getUbicacion(),hecho.getFecha(),hecho.getOrigen());
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(hechoDTO));
        } catch (Exception e){
            return new ResponseEntity<>(new HechoDTO("null"," null","null"),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HechoDTO> obtenerHecho(@PathVariable String id) {
        HechoDTO retorno = fachadaFuente.buscarHechoXId(id);
        if(Objects.isNull(retorno)){
            return new ResponseEntity<>(new HechoDTO("null","null","null"),HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(retorno);
    }
//Ver esto
    @PatchMapping("/{id}") //DEBATIBLE
    public ResponseEntity<HechoDTO> corregirEstado(@PathVariable String id, @RequestBody Hecho body) {
        HechoDTO retorno = fachadaFuente.buscarHechoXId(id);
        if(Objects.isNull(retorno)){
            return new ResponseEntity<>(new HechoDTO("null","null","null"),HttpStatus.BAD_REQUEST);
        }
        List<String> ayuda = new ArrayList<>();
        ayuda.addAll(retorno.etiquetas());
        ayuda.addAll(body.getEtiquetas());
        //NO ESTOY SEGURO DE ESTO
        //NO VOY A CAMBIAR LO QUE HAY DESDE BODY, QUE SERIA ALL. DE TODAS MANERASNO ESTOY SEGURO
        retorno = fachadaFuente.agregar(new HechoDTO(id,retorno.nombreColeccion(), retorno.titulo(), ayuda, retorno.categoria(), retorno.ubicacion(), retorno.fecha(), retorno.origen()));
        return ResponseEntity.ok(retorno);
    }
}

