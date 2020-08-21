package ph.edu.up.ics.oceans13mod7.rest.request;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class CatchJson {

    @SerializedName("latitude")
    @ColumnInfo(name = "latitude")
    public double latitude;

    @SerializedName("longitude")
    @ColumnInfo(name = "longitude")
    public double longitude;

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    public String timestamp;

    @SerializedName("relatedphoto")
    @ColumnInfo(name = "relatedphoto")
    public String relatedphoto;

}
