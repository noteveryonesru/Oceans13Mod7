package ph.edu.up.ics.oceans13mod7.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Record.class}, version = 5)
public abstract class OceansDatabase extends RoomDatabase {
    private static OceansDatabase oceansDatabaseInstance = null;
    public abstract RecordDao recordDao();
    public static OceansDatabase getInstance(Context context){
        if (oceansDatabaseInstance == null){
            oceansDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(), OceansDatabase.class, "oceans13mod7-database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return oceansDatabaseInstance;
    }

    public static class AsyncInsert extends AsyncTask<Void, Void, Void>{
        private OceansDatabase db;
        private int sessionId;
        private double latitude;
        private double longitude;
        private double heading;
        private double speed;

        public AsyncInsert(OceansDatabase db, int sessionId, double latitude, double longitude, double heading, double speed){
            this.db = db;
            this.sessionId = sessionId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.heading = heading;
            this.speed = speed;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.recordDao().insertRecord(new Record(sessionId, latitude, longitude, heading, speed));
            return null;
        }
    }

}
