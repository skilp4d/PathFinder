package com.appdev.deepak.pathfinder;

/**
 * Created by 209de on 12/3/2015.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;


public class KNN implements Serializable{
    ArrayList<UniqueClass> classes;
    public KNN(){
        this.classes = new ArrayList<UniqueClass>();
    }

    void Train(double input[], int id){
        UniqueClass temp = null;
        for(int i=0; i<this.classes.size(); i++){
            if(classes.get(i).id == id){
                temp = classes.get(i);
                break;
            }
        }
        if(temp == null){
            System.out.println("New class created : " + (char)id );
            temp = new UniqueClass(input.length, id);
            classes.add(temp);
        }
        temp.Train(input);
    }

    int Query(double input[]){
        int classs;
        classs = classes.get(0).id;
        double min = classes.get(0).Queryy(input);
        for(int i=1;i<classes.size();i++){
            double temp = classes.get(i).Queryy(input);
            if(temp < min){
                min = temp;
                classs = classes.get(i).id;
            }
        }
        return classs;
    }

    double[] ConfidanceQuery(double input[]){
        double distances[] = new double[classes.size()];
        double min =  classes.get(0).Queryy(input);
        for(int i=0;i<classes.size();i++){
            distances[i] = classes.get(i).Queryy(input);
            if(distances[i] < min){
                min = distances[i];
            }
        }
        for(int i=0;i<classes.size();i++){
            double temp = distances[i];
            temp = temp/min;
            temp = 1.0/temp;
            distances[i] = temp;
        }
        return distances;
    }
}

class UniqueClass implements Serializable{
    int id;
    double freq;
    double vector[];
    public UniqueClass(int classSize,int id) {
        this.id = id;
        this.freq = 0;
        this.vector = new double[classSize];
        Arrays.fill(vector, 0.0);
    }
    void Train(double input[]){
        for(int i=0; i<input.length; i++){
            double temp = this.vector[i];
            temp = temp*freq;
            temp += input[i];
            temp = temp/(freq + 1);
            this.vector[i] = temp;
        }
        freq++;
    }

    double Queryy(double input[]){
        double ans=0;
        for(int i=0; i<input.length; i++){
            double diff = input[i] - this.vector[i];
            ans = ans + diff*diff;
        }
        //System.out.println("Query "+ this.id + " : " + ans);
        return ans;
    }
}
