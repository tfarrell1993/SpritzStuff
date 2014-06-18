package com.spritzinc.android.sdk.sample.helloworld;

import com.spritzinc.android.sdk.SpritzSDK;

/**
 * Created by avanha on 1/2/14.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();


        SpritzSDK.init(this,
        	"terence.m.farrell@gmail.com", // 4524122930975538943
       		"53960584e4b04a19d675937d",
       		"https://sdk.spritzinc.com/js/1.0/examples/login_success.html"
        );
    }
}
