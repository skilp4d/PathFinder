package com.appdev.deepak.pathfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


public class TrainDash extends AppCompatActivity {

    //public static final String TAG = CameraActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.squarecamera__CameraFullScreenTheme);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.squarecamera__activity_camera);
        Intent temp = getIntent();
        String dataMA = temp.getExtras().getString("Test/Train");
        Toast.makeText(getApplicationContext(),dataMA,Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            if(dataMA.equals("TRAIN")){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, CameraFragment.newInstance(), CameraFragment.TAG)
                        .commit();
            }else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TestFragment.newInstance(), TestFragment.TAG)
                        .commit();
            }

        }
    }

    public void returnPhotoUri(Uri uri) {
        Intent data = new Intent();
        data.setData(uri);

        if (getParent() == null) {
            setResult(RESULT_OK, data);
        } else {
            getParent().setResult(RESULT_OK, data);
        }

        finish();
    }

    public void onCancel(View view) {
        getSupportFragmentManager().popBackStack();
    }
}