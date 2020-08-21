package ph.edu.up.ics.oceans13mod7.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ph.edu.up.ics.oceans13mod7.rest.request.RecordJson;

@Dao
public interface RecordDao {

    @Query("SELECT DISTINCT session_id FROM record")
    public List<Integer> getAllSessionIds();

    @Query("SELECT latitude, longitude, heading, speed, timestamp FROM record where session_id = :session_id")
    public List<RecordJson> getRecordsBySessionId(int session_id);

    @Query("SELECT id FROM record")
    public List<Integer> getIds();

    @Insert
    public void insertRecord(Record record);

    @Query("DELETE FROM record")
    public void nukeRecords();

}
