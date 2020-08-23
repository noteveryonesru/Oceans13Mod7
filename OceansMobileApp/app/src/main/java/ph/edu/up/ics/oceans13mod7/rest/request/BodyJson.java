package ph.edu.up.ics.oceans13mod7.rest.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BodyJson {

    @SerializedName("MacAddress")
    public String macAddress;
    @SerializedName("ArrayOfSessions")
    public List<SessionJson> arrayOfSessions;

    public BodyJson(String macAddress, List<SessionJson> arrayOfSessions){
        this.macAddress = macAddress;
        this.arrayOfSessions = arrayOfSessions;
    }

}
