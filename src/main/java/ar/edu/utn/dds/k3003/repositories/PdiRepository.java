package ar.edu.utn.dds.k3003.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ar.edu.utn.dds.k3003.model.Pdi;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class PdiRepository {
    private List<Pdi> pdis;
    private Long seqId;
    public PdiRepository(){
        pdis = new ArrayList<>();
    }
    public List<Pdi> allPdis(){
        return this.pdis;
    }
    public void setSeqId(Long secuencia){
        this.seqId = secuencia;
    }
    public Pdi save(Pdi pdiGuardar){
        if (Objects.isNull(pdiGuardar.getId())){
            pdiGuardar.setHecho(Long.toString(seqId));
            this.setSeqId(seqId+1);
            pdis.add(pdiGuardar);
        }
        return pdiGuardar;
    }

    public Pdi findById(String id_pdi){
       Pdi pdiEncontrado = pdis.stream()
        .filter(hecho -> hecho.getId().equals(id_pdi))
        .findFirst()
        .orElseThrow(null);
       return pdiEncontrado;
    }
}
