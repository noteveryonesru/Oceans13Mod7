package ph.edu.up.ics.oceans13mod7.rest.request;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class CatchJson {

    @SerializedName("Latitude")
    @ColumnInfo(name = "latitude")
    public double latitude;

    @SerializedName("Longitude")
    @ColumnInfo(name = "longitude")
    public double longitude;

    @SerializedName("TimeStamp")
    @ColumnInfo(name = "timestamp")
    public String timestamp;

    @SerializedName("RelatedPhoto")
    @ColumnInfo(name = "relatedphoto")
    public String relatedphoto;

}
