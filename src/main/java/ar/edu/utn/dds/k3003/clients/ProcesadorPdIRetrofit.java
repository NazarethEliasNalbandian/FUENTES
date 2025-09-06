package ar.edu.utn.dds.k3003.clients;

import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

@Service
public interface ProcesadorPdIRetrofit {
    @POST("/api/pdis")
    Call<PdIDTO> procesar(@Body PdIDTO pdi);
}

