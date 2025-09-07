package ar.edu.utn.dds.k3003.facades;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaSolicitudes {

  SolicitudDTO agregar(SolicitudDTO solicitudDTO);

  SolicitudDTO modificar(String solicitudId, EstadoSolicitudBorradoEnum esta,
      String descripcion) throws NoSuchElementException;

  List<SolicitudDTO> buscarSolicitudXHecho(String hechoId);
  SolicitudDTO buscarSolicitudXId(String solicitudId);

  boolean estaActivo(String unHechoId);

  void setFachadaFuente(FachadaFuente fuente);


}
