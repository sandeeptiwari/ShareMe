package share.techiesandeep.com.shareme;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.internal.AppCall;

/**
 * Created by Sandeep on 24-07-2015.
 */
public class APP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());

    }
}
