package com.example.heartonomi_7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.heartonomi_7.Model.Patient;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Realm realm;
    private Patient user;

    EditText editName;
    EditText editUsername;
    EditText editPassword;
    EditText editWeight;
    EditText editHeight;
    Button  btnSignup;
    Button btnLogin;
    private JsonPlaceHolderApi userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        //Only use this if required
//        realm.beginTransaction();
//        realm.deleteAll();
//        realm.commitTransaction();

        editName = findViewById(R.id.name);
        editUsername = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
        editWeight = findViewById(R.id.weight);
        editHeight = findViewById(R.id.height);
        btnSignup = findViewById(R.id.btn_Signup);
        btnLogin = findViewById(R.id.btn_Login);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.0.180:8000/api/") //insert the API URL here
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        userService = ApiClient.getUserService();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter name", Toast.LENGTH_LONG).show();
                    editName.requestFocus();
                } else if (editUsername.length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter username", Toast.LENGTH_LONG).show();
                    editUsername.requestFocus();
                } else if (editPassword.length() ==0) {
                    Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_LONG).show();
                    editPassword.requestFocus();
                }else if (editWeight.length() ==0) {
                    Toast.makeText(MainActivity.this, "Enter weight", Toast.LENGTH_LONG).show();
                    editWeight.requestFocus();
                }else if (editHeight.length() ==0) {
                    Toast.makeText(MainActivity.this, "Enter height", Toast.LENGTH_LONG).show();
                    editHeight.requestFocus();
                } else {

                    try{
                        createPatient();
//                        realm.beginTransaction();
//                        user = realm.createObject(Patient.class);
//                        user.setName(editName.getText().toString());
//                        user.setUsername(editUsername.getText().toString());
//                        user.setPassword(editPassword.getText().toString());
//                        user.setWeight(editWeight.getText().toString());
//                        user.setHeight(editHeight.getText().toString());
//                        realm.commitTransaction();
//                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();

                    } catch (RealmPrimaryKeyConstraintException e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Fail", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        //this.openNewActivity();   //delete later
    }

    public void openNewActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void createPatient(){
        Patient post = new Patient(editName.getText().toString(),editUsername.getText().toString(),editPassword.getText().toString(),editWeight.getText().toString(),editHeight.getText().toString());

        Call<Patient> call = userService.createPatient(post);
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Success ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}