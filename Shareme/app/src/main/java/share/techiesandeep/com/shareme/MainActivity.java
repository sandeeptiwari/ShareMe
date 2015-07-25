package share.techiesandeep.com.shareme;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity implements  FragmentChangeListener{

    private MainFragment frag;
    private ShareFragment shareFragment;
    private String from = "main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if(findViewById(R.id.container) != null){

            if (savedInstanceState != null) {
                return;
            }
            frag = new MainFragment();
            FragmentTransaction transaction = getTransaction();
            transaction.add(R.id.container, frag);
            transaction.addToBackStack(null);
            transaction.commit();
            from = "main";

        }
    }

    private FragmentTransaction getTransaction(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        return transaction;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(from.equals("main"))
            frag.onActivityResult(requestCode, resultCode, data);
        else if(from.equals("share"))
            shareFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void changeFragment(String args) {
        shareFragment = new ShareFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_name", args);
        shareFragment.setArguments(bundle);

        FragmentTransaction transaction = getTransaction();
        transaction.replace(R.id.container, shareFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        from = "share";
    }
    @Override
    public void setFrom(String where){
        from = where;
    }
}
