package ph.edu.up.ics.oceans13mod7.rest.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionJson {

    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("EndTime")
    public String endTime;
    @SerializedName("ArrayOfRecords")
    public List<RecordJson> arrayOfRecords;
    @SerializedName("ArrayOfCatches")
    public List<CatchJson> arrayOfCatches;

    public SessionJson(String startTime, String endTime, List<RecordJson>arrayOfRecords, List<CatchJson> arrayOfCatches){
        this.startTime = startTime;
        this.endTime = endTime;
        this.arrayOfRecords = arrayOfRecords;
        this.arrayOfCatches = arrayOfCatches;
    }

}
