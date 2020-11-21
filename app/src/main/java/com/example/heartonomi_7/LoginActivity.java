package com.example.heartonomi_7;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heartonomi_7.Model.Patient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private Button btnLogin, btnSignup;
    private Realm realm;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        realm = Realm.getDefaultInstance();

        editUsername = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_Login);
        btnSignup = findViewById(R.id.btn_Signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupActivity();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsername.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Enter username", Toast.LENGTH_LONG).show();
                    editUsername.requestFocus();
                } else if (editPassword.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_LONG).show();
                    editPassword.requestFocus();
                } else {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setUsername(editUsername.getText().toString());
                    loginRequest.setPassword(editPassword.getText().toString());
                    loginUser(loginRequest);
                    //-------
//                    realm = Realm.getDefaultInstance();
//                    RealmResults<Patient> realmObjects = realm.where(Patient.class).findAll();
//                    for (Patient myRealmObject : realmObjects) {
//                        if (editUsername.getText().toString().equals(myRealmObject.getUsername()) && editPassword.getText().toString().equals(myRealmObject.getPassword())) {
//                            loginSuccess(editUsername.getText().toString());
//                        } else {
//                            Toast.makeText(LoginActivity.this, "Username or Password is invalid", Toast.LENGTH_LONG).show();
//                        }
//                    }
                    //--------
                }
            }
        });

        //startActivity(new Intent(LoginActivity.this,UserProfile.class).putExtra("username","jun707")); //delete later
    }

    public void loginSuccess(String username){
        Intent intent = new Intent(this, UserProfile.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void signupActivity(){
        Intent intent = new Intent(this, com.example.heartonomi_7.MainActivity.class);
        startActivity(intent);
    }


    public void loginUser(final LoginRequest loginRequest){
        Call<List<LoginResponse>> loginResponseCall = ApiClient.getUserService().loginUser();
        loginResponseCall.enqueue(new Callback<List<LoginResponse>>() {
            @Override
            public void onResponse(Call<List<LoginResponse>> call, Response<List<LoginResponse>> response) {
                if(response.isSuccessful()){
                    List<LoginResponse> loginResponse = response.body();
                    for (LoginResponse user : loginResponse) {
                            String username = loginRequest.getUsername();
                            String password = loginRequest.getPassword();
                            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                                startActivity(new Intent(LoginActivity.this,UserProfile.class).putExtra("username",username));
                            finish();
                            break;
                            }else{
                                Toast.makeText(LoginActivity.this, "Username/password incorrect", Toast.LENGTH_LONG).show();
                            }
                    }
                    //finish();
                }else{
                    String message = "Error has occured";
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<LoginResponse>> call, Throwable t) {
                String message = t.getLocalizedMessage();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
//        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().loginUser();
//        loginResponseCall.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if(response.isSuccessful()){
//                    LoginResponse loginResponse = response.body();
//                    startActivity(new Intent(LoginActivity.this,UserProfile.class).putExtra("username",loginResponse));
//                    Log.v("Look At this", response.body().toString());
//                    finish();
//                }else{
//                    String message = "Error has occured";
//                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                String message = t.getLocalizedMessage();
//                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void getPosts(String username){
        Call<Patient> call = jsonPlaceHolderApi.getPatients(username);
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(LoginActivity.this, call.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}