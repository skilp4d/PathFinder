package com.appdev.deepak.pathfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by 209de on 11/28/2015.
 */
public class NeuralNet {
    boolean instantiated = false;
    FileOutputStream fos = null;
    public static boolean beingAccessed = false ;
    ObjectOutputStream oos = null;
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    File file;
    Context context;
    KNN network;
    String filename = "knnData.ser";
    private static NeuralNet instance = new NeuralNet();
    private NeuralNet(){
        //Toast.makeText(context, "Loading data !!!",Toast.LENGTH_LONG).show();
        Log.e("FILE","Data loaded !!!");
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PathFinder"
        );
        file = new File(
                mediaStorageDir.getPath() + File.separator + filename
        );

        try {
            if(file.createNewFile()){
                network = new KNN();
            }else{
                fis = new FileInputStream(file);
                ois = new ObjectInputStream(fis);
                network = (KNN)ois.readObject();
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public static NeuralNet getInstance(){
        return instance;
    }

    public static void setContext(Context context){
        instance.context = context;
    }

    public void finalise(){
        instance.instantiated = true;
    }
    public void train(double inp[],int id){
        //Toast.makeText(instance.context,"Training started ",Toast.LENGTH_SHORT).show();
        instance.network.Train(inp,id);
        //Toast.makeText(instance.context,"Training finished",Toast.LENGTH_SHORT).show();
    }

    public static int query(double inp[]){
        return instance.network.Query(inp);
    }

    public void saveData(Context con){
        new SaveAsync().execute();
        Toast.makeText(con,"Saved ",Toast.LENGTH_SHORT).show();
    }

    public static class SaveAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            NeuralNet temp = NeuralNet.getInstance();
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "PathFinder"
            );
            final File file = new File(
                    mediaStorageDir.getPath() + File.separator + instance.filename
            );
            try {
                instance.fos = new FileOutputStream(file);
                instance.oos = new ObjectOutputStream(instance.fos);
                instance.oos.writeObject(instance.network);
                instance.fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
