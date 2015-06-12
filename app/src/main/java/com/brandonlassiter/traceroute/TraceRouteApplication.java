package com.brandonlassiter.traceroute;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by brandon on 6/11/15.
 */
public class TraceRouteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "4Y6x4vJRQnyWiBtnmuxRRMGJJWQYh6U6TjXy047I", "YAVy3koE5zOUto783Vngzy0NmPGukCaSr46cKA1A");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());

    }
}
