package share.techiesandeep.com.shareme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sandeep on 25-07-2015.
 */
public class ShareFragment extends Fragment implements View.OnClickListener{

    private Activity mActivity;
    private View mView;
    private TextView mWelcometxt;
    private Button mBtnSharetxt, mBtnShareImg, mBtnShareVideo;
    private String mUserName;
    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_VIDEO = 2;
    private String dataPath;
    private CallbackManager mCallbackManager;
    private LoginManager manager;
    private String from = "text";
    private FragmentChangeListener fragmentChangeListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        fragmentChangeListener = (FragmentChangeListener)mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mUserName = bundle.getString("user_name");
        return inflater.inflate(R.layout.fragment_share, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        fragmentChangeListener.setFrom("share");
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        manager = LoginManager.getInstance();
        manager.registerCallback(mCallbackManager,  new FacebookCallback<LoginResult>(){

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("ShareFragment", "Success");

                if(from.equals("text")){
                    publishText();
                }else if(from.equals("image")){
                   pickPicture();
                }else if(from.equals("video")){
                   pickVideo();
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        initUI();
    }

    private void initUI(){
        mView = getView();
        mWelcometxt = (TextView)mView.findViewById(R.id.txt_welcome);
        mWelcometxt.setText("Welcome :"+mUserName);
        mBtnSharetxt = (Button)mView.findViewById(R.id.txt_share);
        mBtnShareImg = (Button)mView.findViewById(R.id.img_share);
        mBtnShareVideo = (Button)mView.findViewById(R.id.video_share);

        mBtnSharetxt.setOnClickListener(this);
        mBtnShareImg.setOnClickListener(this);
        mBtnShareVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.txt_share:
                from = "text";
                manager.logInWithPublishPermissions(mActivity, Arrays.asList("publish_actions"));
                break;
            case R.id.img_share:{
                from = "image";
                manager.logInWithPublishPermissions(mActivity, Arrays.asList("publish_actions"));
            }
                break;
            case R.id.video_share:{
                from = "video";
                manager.logInWithPublishPermissions(mActivity, Arrays.asList("publish_actions"));
            }
                break;
        }
    }

    private void pickPicture(){
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() <= 0) {
            Log.d("ShareFragment", "no image picker intent on this hardware");
            return;
        }
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, SELECT_PICTURE);
    }
    private void publishPicture(String path){
        Log.i("ShareFragment image", path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(path, options);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);
    }

    private void pickVideo(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("video/*");

        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() <= 0) {
            Log.d("ShareFragment", "no video picker intent on this hardware");
            return;
        }

        startActivityForResult(intent, SELECT_VIDEO);
    }
    private void publishVideo(String path){
        Log.i("ShareFragment video", path);
        File file = new File(path);
        Uri videoUrl = Uri.fromFile(file);
        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoUrl)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(video)
                .build();
        ShareApi.share(content, null);
    }
    private void publishText(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("www.techiesandeep.com/#/facebook_in_android_studio"))
                .build();
        ShareApi.share(content, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURE ||
                      requestCode == SELECT_VIDEO){
            Uri selectedImageUri = data.getData();
            dataPath = getPath(selectedImageUri);
            if(requestCode == SELECT_PICTURE )
            {
                publishPicture(dataPath);
            }else if(requestCode == SELECT_VIDEO){
                publishVideo(dataPath);
            }
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = mActivity.getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }
}
