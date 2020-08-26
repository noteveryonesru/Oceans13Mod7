package ph.edu.up.ics.oceans13mod7.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import ph.edu.up.ics.oceans13mod7.Utils;

@Entity
public class Catch {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "session_id")
    public int session_id;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "relatedphoto")
    public String relatedPhoto;

    @ColumnInfo(name = "timestamp")
    public String timestamp;


    public Catch(int session_id, double latitude, double longitude, String relatedPhoto){
        this.session_id = session_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.relatedPhoto = relatedPhoto;
        Date date = new Date(System.currentTimeMillis());
        this.timestamp = Utils.simpleDateFormat.format(date);
    }
}
