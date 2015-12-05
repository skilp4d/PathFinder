package com.appdev.deepak.pathfinder;


import java.util.Arrays;

public class Mat{
    int n,m;
    double arr[][];
    public Mat(double arr[][]){
        this.arr = arr;
        this.n = arr.length;
        this.m = arr[0].length;
    }
    public Mat(int n, int m){
        this.arr = new double[n][m];
        this.n = n;
        this.m = m;
    }

    public void Init(double in){
        Arrays.fill(this.arr, in);
    }

    public void Transpose(){
        for(int i=0;i<n;i++){
            for(int j=i;j<m;j++){
                double temp = this.arr[i][j];
                this.arr[i][j] = this.arr[j][i];
                this.arr[j][i] = temp;
            }
        }
    }

    public void String(){
        for(int i=0;i<n;i++){
            for(int j=0; j < m; j++){
                System.out.print(arr[i][j]);
            }
            System.out.println();
        }
    }

    public double[] TolinearArray(){
        double temp[];
        temp = new double[this.n*this.m];
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                temp[i*m + j] = this.arr[i][j];
            }
        }
        return temp;
    }
}
