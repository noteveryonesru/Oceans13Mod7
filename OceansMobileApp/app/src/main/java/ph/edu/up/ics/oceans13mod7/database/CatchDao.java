package ph.edu.up.ics.oceans13mod7.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ph.edu.up.ics.oceans13mod7.rest.request.CatchJson;

@Dao
public interface CatchDao {
    @Query("SELECT latitude, longitude, relatedphoto, timestamp FROM catch where session_id = :session_id")
    public List<CatchJson> getCatchesBySessionId(int session_id);

    @Insert
    public void insertCatch(Catch toInsert);

    @Query("DELETE FROM catch")
    public void nukeRecords();
}
