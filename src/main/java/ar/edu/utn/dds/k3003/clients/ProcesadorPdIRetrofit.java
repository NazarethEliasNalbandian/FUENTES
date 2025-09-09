package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dto.PdICreateRequest;
import ar.edu.utn.dds.k3003.clients.dto.ProcesamientoResponseDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProcesadorPdIRetrofit {
    @POST("/api/pdis")
    Call<ProcesamientoResponseDTO> procesar(@Body PdICreateRequest pdi);
}
