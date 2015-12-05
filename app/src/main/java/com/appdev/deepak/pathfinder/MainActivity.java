package com.appdev.deepak.pathfinder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.gc.materialdesign.views.ButtonRectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ButtonRectangle test,train;
    File file;
    String filename;
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    int temp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filename = "Network.ser";
        test = (ButtonRectangle)findViewById(R.id.test);
        train = (ButtonRectangle)findViewById(R.id.train);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toTrain = new Intent(MainActivity.this,TrainDash.class);
                toTrain.putExtra("Test/Train","TEST");
                startActivity(toTrain);
            }
        });
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toTrain = new Intent(MainActivity.this,TrainDash.class);
                toTrain.putExtra("Test/Train","TRAIN");
                startActivity(toTrain);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "BlindWalker"
        );
        file = new File(
                mediaStorageDir.getPath() + File.separator + filename
        );
        try {
            //Log.e("File Dest",file.getAbsolutePath());
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeInt(temp);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //NeuralNet.getInstance().saveData(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //NeuralNet.setContext(getApplicationContext());
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "BlindWalker"
        );
        file = new File(
                mediaStorageDir.getPath() + File.separator + filename
        );
        try {
            if(file.createNewFile()){
                Toast.makeText(getApplicationContext(),"Opened first time",Toast.LENGTH_LONG).show();
            }else{
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                temp = (int)ois.readInt();
                temp++;
                Toast.makeText(getApplicationContext(),"Opened "+ temp + " times !!",Toast.LENGTH_LONG).show();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
