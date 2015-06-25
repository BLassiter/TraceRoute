package com.brandonlassiter.traceroute;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class LoginActivity extends Activity {

    private ProgressDialog progressDialog;
    private boolean isImageUrlCaptureFinished = false;
    private int numImageUrlsReceived = 0;
    private Handler h = new Handler();

    public static final String TAG = "LOGIN_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ((Button)findViewById(R.id.authButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onLoginClick(v);

            }
        });

        ((Button) findViewById(R.id.authButton)).setText("Login with Facebook");


        ParseInstallation.getCurrentInstallation().saveInBackground();

    }


    public void onLoginClick(View v) {

        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)


        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();

                if (user == null) {

                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();

                } else if (user.isNew()) {

                    Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(openMainActivity);

                } else {
                    Log.d("Login", "User logged in through Facebook!");

                    Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(openMainActivity);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "Result: " + resultCode);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);



    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

}