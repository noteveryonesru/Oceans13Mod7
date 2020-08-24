package ph.edu.up.ics.oceans13mod7.rest;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ph.edu.up.ics.oceans13mod7.UploadLocker;
import ph.edu.up.ics.oceans13mod7.database.OceansDatabase;
import ph.edu.up.ics.oceans13mod7.rest.request.BodyJson;
import ph.edu.up.ics.oceans13mod7.rest.request.CatchJson;
import ph.edu.up.ics.oceans13mod7.rest.request.RecordJson;
import ph.edu.up.ics.oceans13mod7.rest.request.SessionJson;
import ph.edu.up.ics.oceans13mod7.rest.response.UploadResponseJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class OceansClient {

    private OceansApi service;

    public OceansClient(String oceansURL) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + oceansURL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(OceansApi.class);
    }

    public void uploadRecords(UploadLocker uploadLocker, OceansDatabase db, String macAddress) {
        if (service != null) {
            UploadRecordsAsync runner = new UploadRecordsAsync(uploadLocker, service, db, macAddress);
            runner.execute();
        }
    }

    public static class UploadRecordsAsync extends AsyncTask<Void, Void, Void> {

        private OceansDatabase db;
        private String macAddress;
        private OceansApi service;
        private UploadLocker uploadLocker;

        public UploadRecordsAsync(UploadLocker uploadLocker, OceansApi service, OceansDatabase db, String macAddress) {
            this.db = db;
            this.macAddress = macAddress;
            this.service = service;
            this.uploadLocker = uploadLocker;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //db.recordDao().insertRecord(new Record(sessionId, latitude, longitude, heading, speed));
            List<Integer> sessionIds = db.recordDao().getAllSessionIds();
            List<SessionJson> listOfSessions = new ArrayList<SessionJson>();
            for (int sessionId : sessionIds) {
                List<RecordJson> tempRecords = db.recordDao().getRecordsBySessionId(sessionId);
                List<CatchJson> tempCatches = new ArrayList<CatchJson>();
                String startTime = tempRecords.get(0).timestamp;
                String endTime = tempRecords.get(tempRecords.size() - 1).timestamp;
                SessionJson session = new SessionJson(startTime, endTime, tempRecords, tempCatches);
                listOfSessions.add(session);
            }
            BodyJson toSend = new BodyJson(macAddress, listOfSessions);
            Log.i(TAG, macAddress + ", " + listOfSessions.toString());
            Callback<UploadResponseJson> uploadResponseCallback = generateUploadResponseCallback(db, uploadLocker);
            service.uploadSessions(toSend).enqueue(uploadResponseCallback);
            return null;
        }

        public Callback<UploadResponseJson> generateUploadResponseCallback(final OceansDatabase db, final UploadLocker uploadLocker) {
            return new Callback<UploadResponseJson>() {
                @Override
                public void onResponse(Call<UploadResponseJson> call, Response<UploadResponseJson> response) {
                    if (response.isSuccessful()) {
                        try {
                            UploadResponseJson uploadResponse = response.body();
                            if (uploadResponse != null && uploadResponse.status == 1) {
                                OceansDatabase.AsyncNuke runner = new OceansDatabase.AsyncNuke(db);
                                runner.execute();
                            }
                        } catch (Exception e) {
                            Log.i("OceansTag", e.getMessage());
                        }
                    } else {
                        Log.i("OceansTag", "Something Failed (onResponse)");
                    }
                    if (uploadLocker != null) {
                        uploadLocker.unlock();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponseJson> call, Throwable t) {
                    uploadLocker.toastError(t.getMessage());
                    uploadLocker.unlock();
                }
            };
        }

    }


}
