package ph.edu.up.ics.oceans13mod7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "session_id")
    public int session_id;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "bearing")
    public double bearing;

    @ColumnInfo(name = "acceleration")
    public double acceleration;

    @ColumnInfo(name = "timestamp")
    public long timestamp;


    public Record(int session_id, double latitude, double longitude, double bearing, double acceleration){
        this.session_id = session_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
        this.acceleration = acceleration;
        this.timestamp = System.currentTimeMillis();
    }

}
