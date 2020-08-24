package ph.edu.up.ics.oceans13mod7.rest;

import ph.edu.up.ics.oceans13mod7.rest.request.BodyJson;
import ph.edu.up.ics.oceans13mod7.rest.response.UploadResponseJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OceansApi {
    @POST("save")
    Call<UploadResponseJson> uploadSessions(@Body BodyJson body);
}
