package ph.edu.up.ics.oceans13mod7.rest.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionJson {

    @SerializedName("startTime")
    public String startTime;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("arrayOfRecords")
    public List<RecordJson> arrayOfRecords;
    @SerializedName("arrayOfCatches")
    public List<CatchJson> arrayOfCatches;

    public SessionJson(String startTime, String endTime, List<RecordJson>arrayOfRecords, List<CatchJson> arrayOfCatches){
        this.startTime = startTime;
        this.endTime = endTime;
        this.arrayOfRecords = arrayOfRecords;
        this.arrayOfCatches = arrayOfCatches;
    }

}
