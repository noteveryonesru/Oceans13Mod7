package ph.edu.up.ics.oceans13mod7.rest.request;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class RecordJson {

    @SerializedName("latitude")
    @ColumnInfo(name = "latitude")
    public double latitude;

    @SerializedName("longitude")
    @ColumnInfo(name = "longitude")
    public double longitude;

    @SerializedName("heading")
    @ColumnInfo(name = "heading")
    public double heading;

    @SerializedName("speed")
    @ColumnInfo(name = "speed")
    public double speed;

    @SerializedName("timestamp")
    @ColumnInfo(name = "timestamp")
    public String timestamp;

}
