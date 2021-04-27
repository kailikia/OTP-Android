package com.edwin.otpapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    APIInterface apiService;

    String myOtp;

    private String phone;

    private TextView show_error_otp;

    private TextView mTextViewCountDown;

    private static final long START_TIME_IN_MILLS=100000;

    private Button resend_otp;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mTimeLeftMilli = START_TIME_IN_MILLS;

    // UI references.
    private AutoCompleteTextView mOTPView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        apiService = APIClient.getClient().create(APIInterface.class);
        mOTPView = (AutoCompleteTextView) findViewById(R.id.otp);
        show_error_otp = (TextView)findViewById(R.id.show_error_otp);
        mTextViewCountDown = (TextView) findViewById(R.id.count_down_text);

        start_timer();

        Button mPhoneSignInButton = (Button) findViewById(R.id.phone_otp_button);
        mPhoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptOTP();
            }
        });

        final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        this.myOtp = prefs.getString("my_otp", "NO");//"NO" is the default value.

        this.phone = prefs.getString("my_phone", "NO");//"NO" is the default value.

        this.resend_otp = (Button) findViewById(R.id.phone_otp);
    }

    private void attemptOTP()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Checking OTP..", Toast.LENGTH_LONG);
        // initiate the Toast with context, message and duration for the Toast
        toast.show();
        // display the Toast

        final String MY_PREFS_NAMED = "MyPrefsFile";
        SharedPreferences prefsd = getSharedPreferences(MY_PREFS_NAMED, MODE_PRIVATE);
        this.myOtp = prefsd.getString("my_otp", "NO");//"NO" is the default value.
        this.phone = prefsd.getString("my_phone", "NO");//"NO" is the default value.

        if (mOTPView.getText().toString().equals(this.myOtp))
        {
                Toast toasti = Toast.makeText(getApplicationContext(), "SUCCESSFUL OTP", Toast.LENGTH_LONG);
                toasti.show();

                final String MY_PREFS_NAME = "MyPrefsFile";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("otp_status", "YES");
                editor.apply();

                Intent myIntent = new Intent(OTPActivity.this, MainActivity.class);
                OTPActivity.this.startActivity(myIntent);
                finish();
        }
         else
                {
                    show_error_otp.setText("OTP is Incorrect.");
                    Toast.makeText(OTPActivity.this, "OTP is incorrect.", Toast.LENGTH_LONG).show();
                }
    }


    public void start_timer()
    {
        this.mCountDownTimer =  new CountDownTimer(mTimeLeftMilli,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftMilli = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

            }
        }.start();

        mTimerRunning = true;
    }

    private void updateCountDownText()
    {
        int minutes = (int) (mTimeLeftMilli / 1000) /60;
        int seconds = (int) (mTimeLeftMilli / 1000) % 60;

        String s = String.format(Locale.getDefault(),"%2d:%2d",minutes,seconds);

        if(minutes == 0 && seconds == 0 ){
            this.resend_otp.setVisibility(View.VISIBLE);
            this.mTextViewCountDown.setText("");

        }
        else
        {
            this.mTextViewCountDown.setText("Re-Send SMS in "+s);
        }

    }

    public void resend_otp(View view)
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Sending OTP....", Toast.LENGTH_LONG); // initiate the Toast with context, message and duration for the Toast
        toast.show(); // display the Toast

        Sms sms = new Sms(this.phone, "Your OTP", "200","1234");

        Call<Sms> call = apiService.createSMS(sms);
        call.enqueue(new Callback<Sms>() {
            @Override
            public void onResponse(Call<Sms> call, Response<Sms> response) {
                int statusCode = response.code();
                Sms sms = response.body();

                if (statusCode == 200) {
                    // MY_PREFS_NAME - a static String variable like:
                    final String MY_PREFS_NAME = "MyPrefsFile";
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("otp_sent", "YES");
                    editor.putString("my_phone", sms.getPhone());
                    editor.putString("my_otp", sms.getOtp());
                    editor.apply();
                }
                else
                {
                    Toast.makeText(OTPActivity.this, "Cant send, code is: "+String.valueOf(statusCode), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Sms> call, Throwable t) {
                // Log error here since request failed
                Toast.makeText(OTPActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

