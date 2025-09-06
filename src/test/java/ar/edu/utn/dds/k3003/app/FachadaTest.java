package ar.edu.utn.dds.k3003.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.model.Coleccion;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FachadaTest {

  public static final String UNA_COL = "unaCol";
  public static final String DESCRIPCION = "1234556";
  Coleccion someDomainObject1;
  Fachada fachada;


  @BeforeEach
  void setUp() {
    someDomainObject1 = new Coleccion("a", "Hola!");
    fachada = new Fachada();
  }

  @Test
  void testAddQuery() {
    fachada.agregar(new ColeccionDTO(UNA_COL, DESCRIPCION));
    val col = fachada.buscarColeccionXId(UNA_COL);

    assertEquals(UNA_COL, col.nombre());
  }

  @Test
  void testRepatedColeccion() {
    fachada.agregar(new ColeccionDTO(UNA_COL, DESCRIPCION));
    assertThrows(IllegalArgumentException.class, () -> {
      fachada.agregar(new ColeccionDTO(UNA_COL, "321"));
    });
  }
}
