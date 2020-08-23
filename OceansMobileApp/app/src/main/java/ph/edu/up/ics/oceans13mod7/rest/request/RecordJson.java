package ph.edu.up.ics.oceans13mod7.rest.request;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class RecordJson {

    @SerializedName("Latitude")
    @ColumnInfo(name = "latitude")
    public double latitude;

    @SerializedName("Longitude")
    @ColumnInfo(name = "longitude")
    public double longitude;

    @SerializedName("Heading")
    @ColumnInfo(name = "heading")
    public double heading;

    @SerializedName("Speed")
    @ColumnInfo(name = "speed")
    public double speed;

    @SerializedName("TimeStamp")
    @ColumnInfo(name = "timestamp")
    public String timestamp;

}
