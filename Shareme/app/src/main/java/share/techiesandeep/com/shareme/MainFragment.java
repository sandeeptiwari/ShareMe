package share.techiesandeep.com.shareme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONObject;
import java.util.Arrays;


/**
 * Created by Sandeep on 24-07-2015.
 */
public class MainFragment extends Fragment{
    private Activity mActivity;
    private View mView;
    private Button mBtnLogin;
    private CallbackManager mCallbackManager;
    private LoginManager manager;
    private FragmentChangeListener fragmentChangeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        fragmentChangeListener = (FragmentChangeListener)mActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentChangeListener.setFrom("main");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    private void initFb(){
        // Callback registration
        mCallbackManager = CallbackManager.Factory.create();
        manager = LoginManager.getInstance();
        //login with permission for getting user profile and email and birthday of user
        manager.logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends",
                 "email", "user_birthday"));
        manager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                String token = accessToken.getToken();
                Log.i("MainFragment", token);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.v("MainFragment", response.toString());

                                try {
                                    String userName = object.optString("name");
                                    fragmentChangeListener.changeFragment(userName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

                /* make the API call */
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.v("MainFragment Friends", response.toString());
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });
    }
    private void initUI(){
        mView = getView();
        mBtnLogin = (Button)mView.findViewById(R.id.btn_fb_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                initFb();
                Log.i("MainFragment", "after login");
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
