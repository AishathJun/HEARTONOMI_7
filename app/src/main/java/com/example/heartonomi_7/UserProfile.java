package com.example.heartonomi_7;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class UserProfile extends AppCompatActivity {
    private LineChart mChart, mChart2, mChart3;
    private Realm realm;
    private Patient currentPatient;
    Button btn_Logout, btn_Submit;
    TextView labelUsername, labelFullname;
    EditText sysBP, diaBP, heartRate;
    LoginResponse loginResponse;
    //String usernameString;

    List<BloodPressureAPI> userBloodPressure;  //blood pressure data belonging to the user

    int predSys, predDia;
    private JsonPlaceHolderApiBP bpService;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_page);

        //module variable
        userBloodPressure = new ArrayList<>();

        mChart = (LineChart) findViewById(R.id.Linechart);
        mChart2 = (LineChart) findViewById(R.id.Linechart2);
        mChart3 = (LineChart) findViewById(R.id.Linechart3);

        labelUsername = findViewById(R.id.u_username);  //renamed it from editUsername
        labelFullname = findViewById(R.id.fname);       //renamed this one
        btn_Submit = findViewById(R.id.btnSubmit);
        btn_Logout = findViewById(R.id.btnlogout);

        sysBP = findViewById(R.id.systolic);
        diaBP = findViewById(R.id.diastolic);
        heartRate = findViewById(R.id.heartrate);

        //replace these lines with...
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.0.180:8000/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        jsonPlaceHolderApiBP = retrofit.create(JsonPlaceHolderApiBP.class);
        //this. it does the same thing
        bpService = ApiClient.getBPService(); //renamed jsonPlaceholdeApiBP to bpService.

        //fetch the all the user data from the login activity. We dont need realm.
        Intent i = getIntent();
        final String usernameString;
        usernameString =  i.getStringExtra("username");
        String name = i.getStringExtra("name");
        String password = i.getStringExtra("password");
        String height = i.getStringExtra("height");
        String weight= i.getStringExtra("weight");

        currentPatient = new Patient(name, usernameString, password, weight, height);


        //Set the text data for UI
        labelUsername.setText(usernameString);
        labelFullname.setText(currentPatient.getName());


        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(sysBP.getText().toString()) || TextUtils.isEmpty(diaBP.getText().toString()) || TextUtils.isEmpty(heartRate.getText().toString())){
                        Toast.makeText(UserProfile.this,"Systolic / Diastolic / Heart rate Required", Toast.LENGTH_LONG).show();
                    }else{
                        //success!!
                        hideKeyboard(); //hide the annoying keyboard.

                        createBP(usernameString);
                        //viewChart();
                    }
                }
            });


//        for (final Patient myRealmObject : realmObjects) {
//            if (s1.equals(myRealmObject.getUsername())) {
//                editName.setText(myRealmObject.getName());
//                btn_Submit = findViewById(R.id.btnSubmit);
//                btn_Submit.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View view) {
//                        if(TextUtils.isEmpty(sysBP.getText().toString()) || TextUtils.isEmpty(diaBP.getText().toString()) || TextUtils.isEmpty(heartRate.getText().toString())){
//                            Toast.makeText(UserProfile.this,"Systolic / Diastolic / Heart rate Required", Toast.LENGTH_LONG).show();
//                        }else{
//                            //addBloodpress(myRealmObject);
//                            createBP();
//                            viewChart();
//                            Date c = Calendar.getInstance().getTime();
//                            final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                            String datetime = dateformat.format(c.getTime());
//
//                            PredictBloodPressureRequest predictBloodPressureRequest = new PredictBloodPressureRequest();
//
//                            predictBloodPressureRequest.setUserName(editUsername.getText().toString());
//                            predictBloodPressureRequest.setSystolic(Integer.parseInt(sysBP.getText().toString()));
//                            predictBloodPressureRequest.setDiastolic(Integer.parseInt(diaBP.getText().toString()));
//                            predictBloodPressureRequest.setHeartRate(Integer.parseInt(heartRate.getText().toString()));
//                            predictBloodPressureRequest.setReadingTime(datetime);
//                            createPredictedBP(predictBloodPressureRequest);
//                        }
//                    }
//                });
//            }
//        }
    }

    //hide keyboard to fix that annoying issue with keyboard still visible after submit
    private void hideKeyboard(){
        Context context = this.getApplicationContext();
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchPatientBloodPressure(currentPatient.getUsername());

        //Ask server for list of blood pressure and process them once received.

    }


    /**
     * Ask the server for a list of blood pressure and adds the blood pressure data
     * belongin to patient in the module variable 'userBloodPressure'
     * @param username
     */
    void fetchPatientBloodPressure(final String username){
        bpService.getBP().enqueue(new Callback<List<BloodPressureAPI>>() {
            @Override
            public void onResponse(Call<List<BloodPressureAPI>> call, Response<List<BloodPressureAPI>> response) {
                List<BloodPressureAPI> bloodPressureList = response.body();
                //List<BloodPressureAPI> userBloodPressure; //BP readings belonging to the user

                //clear the module variable that stores blood pressure objects
                userBloodPressure.clear();

                //We search through the blood pressure list and add the blood pressure belonging to the user to module list
                for(BloodPressureAPI bp : bloodPressureList){
                    if(bp.getUserName().equals(username)){
                        userBloodPressure.add(bp);
                    }
                }

                renderCharts();
                Log.v("server list size", "="+ bloodPressureList.size());
            }

            @Override
            public void onFailure(Call<List<BloodPressureAPI>> call, Throwable t) {

            }
        });
    }

    void renderCharts(){
        drawChart(mChart, "Current Systolic");
    }

    void drawChart(LineChart chart, String chartLabel){
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.invalidate();

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new MyAxisValueFormatter());

        ArrayList<Entry> systolic = new ArrayList<Entry>();
        int hour1 = 0;
        for(BloodPressureAPI bloodPressure : userBloodPressure){
            systolic.add(new Entry(hour1++, bloodPressure.getSystolic() ));
        }

        LineDataSet lineDataSet1 = new LineDataSet(systolic, chartLabel);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    /**
     * Find a patient from a list of patients by username
     * @param realmObjects
     * @param usernameString
     * @return
     */
    private Patient findPatient(RealmResults<Patient> realmObjects, String usernameString) {
        for(Patient patient: realmObjects){
            if(patient.getUsername().equals(usernameString)){
                return patient; //patient found
            }
        }
        return null; //patient not found
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
        final Call<PredictBloodPressureResponse> predictBloodPressureRequestCall = bpService.displayPredict(predictBloodPressureRequest);
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

    private void createBP(String usernameString){
        Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = dateformat.format(c.getTime());

        final BloodPressureAPI post = new BloodPressureAPI(usernameString,
                                                            Integer.parseInt(sysBP.getText().toString()),
                                                            Integer.parseInt(diaBP.getText().toString()),
                                                            Integer.parseInt(heartRate.getText().toString()),
                                                            datetime);

        Call<BloodPressureAPI> call = bpService.createBP(post);
        call.enqueue(new Callback<BloodPressureAPI>() {
            @Override
            public void onResponse(Call<BloodPressureAPI> call, Response<BloodPressureAPI> response) {
                Toast.makeText(UserProfile.this, response.message(), Toast.LENGTH_LONG).show();
                //viewChart();
            }

            @Override
            public void onFailure(Call<BloodPressureAPI> call, Throwable t) {
                Toast.makeText(UserProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void viewChart(String usernameString){
        Call<List<BloodPressureAPI>> call = bpService.getBP();
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
                   // if(bloodPressure.getUserName().equals(usernameString)){
                    //    systolic.add(new Entry(hour1++, bloodPressure.getSystolic() ));
                   // }
                }

                LineDataSet lineDataSet1 = new LineDataSet(systolic, "Current Systolic");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);

                LineData data = new LineData(dataSets);
                mChart.setData(data);
                mChart.invalidate();
//
//                //--------------DIASTOLIC------------------
//                mChart2.setDragEnabled(true);
//                mChart2.setScaleEnabled(false);
//                mChart2.invalidate();
//
//                XAxis xAxis2 = mChart2.getXAxis();
//                xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
//                xAxis2.setLabelRotationAngle(90);
//                xAxis2.setValueFormatter(new MyAxisValueFormatter());
//
//                ArrayList<Entry> diastolic = new ArrayList<Entry>();
////                ArrayList<Entry> predictedD = new ArrayList<Entry>();
//
//                int hour2 = 0;
//
//                List<BloodPressureAPI> bloodPressureList2 =  response.body();
//                for(BloodPressureAPI bloodPressure : bloodPressureList2){
//                    if(bloodPressure.getUserName().equals(s1)){
//                        diastolic.add(new Entry(hour2++, bloodPressure.getDiastolic() ));
//                    }
//                }
//
////                predictedD.add(new Entry(hour2++, predDia));
//
//                LineDataSet lineDataSet2 = new LineDataSet(diastolic, "Current Diastolic");
////                LineDataSet lineDataSet22 = new LineDataSet(predictedD, "Predicted Diastolic");
//
//                ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
//                dataSets2.add(lineDataSet2);
////                dataSets2.add(lineDataSet22);
//
//                LineData data2 = new LineData(dataSets2);
//                mChart2.setData(data2);
//                mChart2.invalidate();
//                //--------------------------------
//                mChart3.setDragEnabled(true);
//                mChart3.setScaleEnabled(false);
//                mChart3.invalidate();
//
//                XAxis xAxis3 = mChart3.getXAxis();
//                xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//                ArrayList<Entry> bloodp = new ArrayList<Entry>();
//
//                List<BloodPressureAPI> bloodPressureList3 =  response.body();
//
//                for(BloodPressureAPI bloodPressure : bloodPressureList3){
//                    if(bloodPressure.getUserName().equals(s1)){
//                        bloodp.add(new Entry(bloodPressure.getDiastolic(),bloodPressure.getSystolic() ));
//                    }
//                }
//
//                LineDataSet lineDataSet3 = new LineDataSet(bloodp, "Current Blood Pressure");
//                ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
//                dataSets3.add(lineDataSet3);
//
//                LineData data3 = new LineData(dataSets3);
//                mChart3.setData(data3);
//                mChart3.invalidate();
            }

            @Override
            public void onFailure(Call<List<BloodPressureAPI>> call, Throwable t) {
                Toast.makeText(UserProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
