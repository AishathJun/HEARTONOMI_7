package com.example.heartonomi_7;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heartonomi_7.Model.BloodPressure;
import com.example.heartonomi_7.Model.BloodPressureAPI;
import com.example.heartonomi_7.Model.Patient;
import com.example.heartonomi_7.Model.PredictBloodPressureRequest;
import com.example.heartonomi_7.Model.PredictBloodPressureResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfile extends AppCompatActivity {
    private LineChart mChart, mChart2, mChart3;
    private Realm realm;
    Button btn_Logout, btn_Submit;
    TextView editUsername, editName;
    EditText sysBP, diaBP, heartRate;
    LoginResponse loginResponse;
    String s1;

    int predSys, predDia;
    private JsonPlaceHolderApiBP jsonPlaceHolderApiBP;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_page);

        mChart = (LineChart) findViewById(R.id.Linechart);
        mChart2 = (LineChart) findViewById(R.id.Linechart2);
        mChart3 = (LineChart) findViewById(R.id.Linechart3);

        editUsername = findViewById(R.id.u_username);
        editName = findViewById(R.id.fname);
        btn_Submit = findViewById(R.id.btnSubmit);
        btn_Logout = findViewById(R.id.btnlogout);

        sysBP = findViewById(R.id.systolic);
        diaBP = findViewById(R.id.diastolic);
        heartRate = findViewById(R.id.heartrate);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.180:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApiBP = retrofit.create(JsonPlaceHolderApiBP.class);

        Intent i = getIntent();
        s1 =  i.getStringExtra("username");
        editUsername.setText(s1);

        realm = Realm.getDefaultInstance();
        RealmResults<Patient> realmObjects = realm.where(Patient.class).findAll();
        for (final Patient myRealmObject : realmObjects) {
            if (s1.equals(myRealmObject.getUsername())) {
                editName.setText(myRealmObject.getName());
                btn_Submit = findViewById(R.id.btnSubmit);
                btn_Submit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(sysBP.getText().toString()) || TextUtils.isEmpty(diaBP.getText().toString()) || TextUtils.isEmpty(heartRate.getText().toString())){
                            Toast.makeText(UserProfile.this,"Systolic / Diastolic / Heart rate Required", Toast.LENGTH_LONG).show();
                        }else{
                            //addBloodpress(myRealmObject);
                            createBP();
                            Date c = Calendar.getInstance().getTime();
                            final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String datetime = dateformat.format(c.getTime());

                            PredictBloodPressureRequest predictBloodPressureRequest = new PredictBloodPressureRequest();

                            predictBloodPressureRequest.setUserName(editUsername.getText().toString());
                            predictBloodPressureRequest.setSystolic(Integer.parseInt(sysBP.getText().toString()));
                            predictBloodPressureRequest.setDiastolic(Integer.parseInt(diaBP.getText().toString()));
                            predictBloodPressureRequest.setHeartRate(Integer.parseInt(heartRate.getText().toString()));
                            predictBloodPressureRequest.setReadingTime(datetime);
                            createPredictedBP(predictBloodPressureRequest);
                        }
                    }
                });
            }
        }
    }

    public void addBloodpress(Patient myRealmObject){
        realm.beginTransaction();

        Number maxId = realm.where(BloodPressure.class).max("id");
        int newKey = (maxId == null) ? 1 : maxId.intValue()+1;

        BloodPressure bloodp = realm.createObject(BloodPressure.class, newKey);

        bloodp.setDiastolic(Integer.parseInt(diaBP.getText().toString()));
        bloodp.setSystolic(Integer.parseInt(sysBP.getText().toString()));
        bloodp.setHearrate(Integer.parseInt(heartRate.getText().toString()));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        String datetime = dateformat.format(c.getTime());

        bloodp.setCurrentTime(datetime);

        myRealmObject.bp.add(bloodp);
        realm.insertOrUpdate(myRealmObject);

        realm.commitTransaction();

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new MyAxisValueFormatter());

        ArrayList<Entry> systolic = new ArrayList<Entry>();
        int hour1 = 0;
        for(BloodPressure bloodpressure : myRealmObject.getBp()) {
            systolic.add(new Entry(hour1++, bloodpressure.getSystolic()));
        }

        LineDataSet lineDataSet1 = new LineDataSet(systolic, "Current Systolic");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        mChart.setData(data);
        mChart.invalidate();
//--------------------------------------------------------------------------------------------------
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(false);

        XAxis xAxis2 = mChart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setLabelRotationAngle(90);
        xAxis2.setValueFormatter(new MyAxisValueFormatter());

        ArrayList<Entry> diastolic = new ArrayList<Entry>();
        int hour = 0;
        for(BloodPressure bloodpressure : myRealmObject.getBp()) {
            diastolic.add(new Entry(hour++, bloodpressure.getDiastolic()));
        }

        LineDataSet lineDataSet2 = new LineDataSet(diastolic, "Current Diastolic");
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        dataSets2.add(lineDataSet2);

        LineData data2 = new LineData(dataSets2);
        mChart2.setData(data2);
        mChart2.invalidate();
//--------------------------------------------------------------------------------------------------
        mChart3.setDragEnabled(true);
        mChart3.setScaleEnabled(false);

        XAxis xAxis3 = mChart3.getXAxis();
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis3.setLabelRotationAngle(90);
        xAxis3.setValueFormatter(new MyAxisValueFormatter());

        ArrayList<Entry> bp = new ArrayList<Entry>();
        for(BloodPressure bloodpressure : myRealmObject.getBp()) {
            bp.add(new Entry(bloodpressure.getSystolic(), bloodpressure.getDiastolic()));
        }

        LineDataSet lineDataSet3 = new LineDataSet(bp, "Current Blood Pressure");
        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
        dataSets3.add(lineDataSet3);

        LineData data3 = new LineData(dataSets3);
        mChart3.setData(data3);
        mChart3.invalidate();
    }

    private class MyAxisValueFormatter extends ValueFormatter{

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
            String datetime = dateformat.format(c.getTime());
            return datetime;
        }
    }

    private void createPredictedBP(final PredictBloodPressureRequest predictBloodPressureRequest){
        final Call<PredictBloodPressureResponse> predictBloodPressureRequestCall = jsonPlaceHolderApiBP.displayPredict(predictBloodPressureRequest);
        predictBloodPressureRequestCall.enqueue(new Callback<PredictBloodPressureResponse>() {
            @Override
            public void onResponse(Call<PredictBloodPressureResponse> call, Response<PredictBloodPressureResponse> response) {
                PredictBloodPressureResponse predictBloodPressureResponses = response.body();
                    int systolicP = predictBloodPressureResponses.getSystolic();
                    int diastolicP = predictBloodPressureResponses.getDiastolic();

                    String mes = "sys:"+systolicP+" dia:"+diastolicP;
                    Log.v("Look here", String.valueOf(systolicP));
                    Toast.makeText(UserProfile.this, mes, Toast.LENGTH_LONG).show();

                    predSys = systolicP;
                    predDia = diastolicP;
//                for (PredictBloodPressureResponse user : predictBloodPressureResponses) {
//                    int systolicP = user.getDiastolic();
//                    int diastolicP = user.getSystolic();
//
//                    String mes = "sys:"+systolicP+" dia:"+diastolicP;
//                    Toast.makeText(UserProfile.this, mes, Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(Call<PredictBloodPressureResponse> call, Throwable t) {
                Toast.makeText(UserProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createBP(){
        Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = dateformat.format(c.getTime());

        final BloodPressureAPI post = new BloodPressureAPI( s1,
                                                            Integer.parseInt(sysBP.getText().toString()),
                                                            Integer.parseInt(diaBP.getText().toString()),
                                                            Integer.parseInt(heartRate.getText().toString()),
                                                            datetime);
        Call<BloodPressureAPI> call = jsonPlaceHolderApiBP.createBP(post);
        call.enqueue(new Callback<BloodPressureAPI>() {
            @Override
            public void onResponse(Call<BloodPressureAPI> call, Response<BloodPressureAPI> response) {
                Toast.makeText(UserProfile.this, "Sent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<BloodPressureAPI> call, Throwable t) {
                Toast.makeText(UserProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void viewChart(){
        Call<List<BloodPressureAPI>> call = jsonPlaceHolderApiBP.getBP();
        call.enqueue(new Callback<List<BloodPressureAPI>>() {
            @Override
            public void onResponse(Call<List<BloodPressureAPI>> call, Response<List<BloodPressureAPI>> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(UserProfile.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                //--------------SYSTOLIC------------------
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(false);
                mChart.invalidate();

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(90);
                xAxis.setValueFormatter(new MyAxisValueFormatter());

                ArrayList<Entry> systolic = new ArrayList<Entry>();
                int hour1 = 0;

                List<BloodPressureAPI> bloodPressureList =  response.body();

                for(BloodPressureAPI bloodPressure : bloodPressureList){
                    if(bloodPressure.getUserName().equals(s1)){
                        systolic.add(new Entry(hour1++, bloodPressure.getSystolic() ));
                    }
                }

                LineDataSet lineDataSet1 = new LineDataSet(systolic, "Current Systolic");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();

                //--------------DIASTOLIC------------------
                mChart2.setDragEnabled(true);
                mChart2.setScaleEnabled(false);
                mChart2.invalidate();

                XAxis xAxis2 = mChart2.getXAxis();
                xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis2.setLabelRotationAngle(90);
                xAxis2.setValueFormatter(new MyAxisValueFormatter());

                ArrayList<Entry> diastolic = new ArrayList<Entry>();
//                ArrayList<Entry> predictedD = new ArrayList<Entry>();

                int hour2 = 0;

                List<BloodPressureAPI> bloodPressureList2 =  response.body();
                for(BloodPressureAPI bloodPressure : bloodPressureList2){
                    if(bloodPressure.getUserName().equals(s1)){
                        diastolic.add(new Entry(hour2++, bloodPressure.getDiastolic() ));
                    }
                }

//                predictedD.add(new Entry(hour2++, predDia));

                LineDataSet lineDataSet2 = new LineDataSet(diastolic, "Current Diastolic");
//                LineDataSet lineDataSet22 = new LineDataSet(predictedD, "Predicted Diastolic");

                ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                dataSets2.add(lineDataSet2);
//                dataSets2.add(lineDataSet22);

                LineData data2 = new LineData(dataSets2);
                mChart2.setData(data2);
                mChart2.invalidate();
                //--------------------------------
                mChart3.setDragEnabled(true);
                mChart3.setScaleEnabled(false);
                mChart3.invalidate();

                XAxis xAxis3 = mChart3.getXAxis();
                xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);

                ArrayList<Entry> bloodp = new ArrayList<Entry>();

                List<BloodPressureAPI> bloodPressureList3 =  response.body();

                for(BloodPressureAPI bloodPressure : bloodPressureList3){
                    if(bloodPressure.getUserName().equals(s1)){
                        bloodp.add(new Entry(bloodPressure.getDiastolic(),bloodPressure.getSystolic() ));
                    }
                }

                LineDataSet lineDataSet3 = new LineDataSet(bloodp, "Current Blood Pressure");
                ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                dataSets3.add(lineDataSet3);

                LineData data3 = new LineData(dataSets3);
                mChart3.setData(data3);
                mChart3.invalidate();
            }

            @Override
            public void onFailure(Call<List<BloodPressureAPI>> call, Throwable t) {
                Toast.makeText(UserProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayChart(){
        Call<List<BloodPressureAPI>> call = jsonPlaceHolderApiBP.getBP();
        call.enqueue(new Callback<List<BloodPressureAPI>>() {
            @Override
            public void onResponse(Call<List<BloodPressureAPI>> call, Response<List<BloodPressureAPI>> response) {
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(false);
                mChart.invalidate();

                XAxis xAxis = mChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(90);
                xAxis.setValueFormatter(new MyAxisValueFormatter());

                mChart2.setDragEnabled(true);
                mChart2.setScaleEnabled(false);
                mChart2.invalidate();

                XAxis xAxis2 = mChart2.getXAxis();
                xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis2.setLabelRotationAngle(90);
                xAxis2.setValueFormatter(new MyAxisValueFormatter());

                mChart3.setDragEnabled(true);
                mChart3.setScaleEnabled(false);
                mChart3.invalidate();

                XAxis xAxis3 = mChart3.getXAxis();
                xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis3.setLabelRotationAngle(90);
                xAxis3.setValueFormatter(new MyAxisValueFormatter());

                ArrayList<Entry> systolic = new ArrayList<Entry>();
                ArrayList<Entry> diastolic = new ArrayList<Entry>();
                ArrayList<Entry> blood_pressure = new ArrayList<Entry>();

                int hour1 = 0;
                List<BloodPressureAPI> bloodPressureList =  response.body();

                for(BloodPressureAPI bloodPressure : bloodPressureList){
                    if(bloodPressure.getUserName().equals(s1)){
                        systolic.add(new Entry(hour1++, bloodPressure.getSystolic()));
                        diastolic.add(new Entry(hour1++, bloodPressure.getDiastolic()));
                        blood_pressure.add(new Entry(bloodPressure.getDiastolic(),bloodPressure.getSystolic()));
                    }
                }

                LineDataSet lineDataSet = new LineDataSet(systolic, "Current Systolic");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();

                LineDataSet lineDataSet2 = new LineDataSet(diastolic, "Current Diastolic");
                ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                dataSets2.add(lineDataSet2);

                LineData data2 = new LineData(dataSets2);
                mChart2.setData(data2);
                mChart2.invalidate();

                LineDataSet lineDataSet3 = new LineDataSet(blood_pressure, "Current Blood Pressure");
                ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
                dataSets3.add(lineDataSet3);

                LineData data3 = new LineData(dataSets3);
                mChart3.setData(data3);
                mChart3.invalidate();
            }

            @Override
            public void onFailure(Call<List<BloodPressureAPI>> call, Throwable t) {
                String message = "Error: "+ t.getMessage();
                Toast.makeText(UserProfile.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
