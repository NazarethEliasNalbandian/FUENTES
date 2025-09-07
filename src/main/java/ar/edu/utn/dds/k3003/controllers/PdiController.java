package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pdi")
public class PdiController {

    private final FachadaFuente fachadaFuente;

    @Autowired
    public PdiController(FachadaFuente fachadaFuente) {
        this.fachadaFuente = fachadaFuente;
    }
    @PostMapping
    public ResponseEntity<PdIDTO> crearPDI(@RequestBody PdIDTO pdi) {
        try {
            return ResponseEntity.ok(fachadaFuente.agregar(pdi));
        } catch (Exception e){
            return new ResponseEntity<>(new PdIDTO("null","null","null","null",LocalDateTime.now(),"null",new ArrayList<>()),HttpStatus.BAD_REQUEST);
        }
    }
} 
