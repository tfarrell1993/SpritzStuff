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
        	"4c79146ca2d934cd8",
       		"64342d83-1d27-4656-97c6-d53591b5d7d6",
       		"https://sdk.spritzinc.com/android/examples/login_success.html"
        );
    }

}
