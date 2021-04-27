package com.edwin.otpapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    APIInterface apiService;

    // UI references.
    private AutoCompleteTextView mPhoneView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mErrorView;
    private String phone;
    private Button mPhoneSignInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String MY_PREFS_NAME = "MyPrefsFile";

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String otp_status = prefs.getString("otp_status", "NO");//"NO" is the default value.
        String otp_sent = prefs.getString("otp_sent", "NO");//"NO" is the default value.

        if (otp_status.equals("YES"))
        {
            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(myIntent);
            finish();

        }
        else if (otp_sent.equals("YES"))
        {
            Intent myIntent = new Intent(LoginActivity.this, OTPActivity.class);
            LoginActivity.this.startActivity(myIntent);
            finish();

        }
        else
        {
            Toast.makeText(this, "OTP Initiated", Toast.LENGTH_SHORT).show();
        }

        apiService = APIClient.getClient().create(APIInterface.class);

        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);
        mErrorView = (TextView) findViewById(R.id.show_error);
        mPhoneSignInButton = (Button) findViewById(R.id.phone_otp_button);
        mPhoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhoneView.getText().toString().length() < 8 || mPhoneView.getText().toString().length() > 11)
                {
                    mErrorView.setText("Incorrect phone number format...");
                }
                else
                {
                    if(mPhoneView.getText().toString().trim().startsWith("7"))
                    {
                        phone = "+254" + mPhoneView.getText().toString().trim();
                        attemptLogin();
                    }
                     else if( mPhoneView.getText().toString().trim().startsWith("0"))
                    {
                        phone = "+254" + mPhoneView.getText().toString().substring(1,mPhoneView.getText().length());
                                attemptLogin();
                    }
                    else {
                        mErrorView.setText("Incorrect phone number format...");
                    }

//                    Toast toast = Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_LONG);
//                    toast.show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        Toast toast = Toast.makeText(getApplicationContext(), "Sending OTP to "+this.phone, Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
        toast.show(); // display the Toast

        Sms sms = new Sms(this.phone, "Your OTP", "200","1234");

        Call<Sms> call = apiService.createSMS(sms);
        call.enqueue(new Callback<Sms>() {
            @Override
            public void onResponse(Call<Sms> call, Response<Sms> response) {
                int statusCode = response.code();
                Sms sms = response.body();
                Toast.makeText(LoginActivity.this, String.valueOf(statusCode), Toast.LENGTH_LONG).show();

                if (statusCode == 200)
                {
                    // MY_PREFS_NAME - a static String variable like:
                    final String MY_PREFS_NAME = "MyPrefsFile";
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("otp_sent", "YES");
                    editor.putString("my_phone",sms.getPhone());
                    editor.putString("my_otp", sms.getOtp());
                    editor.apply();

                    Intent myIntent = new Intent(LoginActivity.this, OTPActivity.class);
                    LoginActivity.this.startActivity(myIntent);
//                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Cant send, code is: "+String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Sms> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

