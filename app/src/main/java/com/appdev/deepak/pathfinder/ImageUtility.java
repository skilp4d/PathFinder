package com.appdev.deepak.pathfinder;

/**
 * Created by 209de on 11/27/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by 209de on 11/24/2015.
 */
public class ImageUtility {
    public static int direction = 1;
    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    public static byte[] convertBitmapStringToByteArray(String bitmapByteString) {
        return Base64.decode(bitmapByteString, Base64.DEFAULT);
    }

    public static Bitmap rotatePicture(Context context, int rotation, byte[] data) {
        Bitmap bitmap = decodeSampledBitmapFromByte(context, data);

        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
        }

        return bitmap;
    }

    public static void savePicture(Context context, Bitmap bitmap, String dir) {
        int cropHeight;
        if (bitmap.getHeight() > bitmap.getWidth()) cropHeight = bitmap.getWidth();
        else                                        cropHeight = bitmap.getHeight();

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropHeight, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "PathFinder"
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }

        switch (dir){
            case "LEFT":
                direction = 0;
                break;
            case "FORWARD":
                direction = 1;
                break;
            case "RIGHT":
                direction = 2;
                break;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(
                mediaStorageDir.getPath() + File.separator + timeStamp + "_" + direction + ".jpg"
        );


        // Saving the bitmap
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap = getResizedBitmap(bitmap, Constants.width, Constants.height);
             // applying edge detection here !!!!

            // Canny edge detector
            //bitmap = applyCanny(bitmap);
            // Sobel edge detector
            bitmap = applySobel(bitmap);
            new AsyncTask<Bitmap , Integer , Integer>(){

                @Override
                protected Integer doInBackground(Bitmap... bitmaps) {
                    Bitmap bitmap = bitmaps[0];
                    NeuralNet temp = NeuralNet.getInstance();
                    double db[][] = new double[bitmap.getHeight()][bitmap.getHeight()];
                    for(int i=0;i<bitmap.getHeight();i++){
                        for(int j=0;j<bitmap.getWidth();j++){
                            int  col = bitmap.getPixel(j,i);
                            col = col & 0xff;
                            db[i][j] = (double)col/64.0;
                        }
                    }

                    Mat img = new Mat(db);
                    double trn[] = img.TolinearArray();
                    temp.train(trn,direction);
                    return null;
                }
            }.execute(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            FileOutputStream stream = new FileOutputStream(mediaFile);


            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        //PCA pca = new PCA();
        // Mediascanner need to scan for the image saved
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.fromFile(mediaFile);
        mediaScannerIntent.setData(fileContentUri);
        context.sendBroadcast(mediaScannerIntent);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight,
                true);
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static Bitmap applyCanny(Bitmap bitmap){
        CannyEdgeDetector detector = new CannyEdgeDetector();
        detector.setLowThreshold(0.5f);
        detector.setHighThreshold(1f);
        detector.setSourceImage(bitmap);
        detector.process();
        return detector.getEdgesImage();
    }

    public static Bitmap applySobel(Bitmap bitmap){
        bitmap = toGrayscale(bitmap);
            double Gx[][], Gy[][], G[][];
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            //int[] pixels = new int[width * height];
            int[][] output = new int[width][height];
            int counter = 0;
            for(int i = 0 ; i < width ; i++ )
            {
                for(int j = 0 ; j < height ; j++ )
                {
                    //output[i][j] = pixels[counter];
                    output[i][j] = bitmap.getPixel(i,j);
                    counter = counter + 1;
                }
            }
            Gx = new double[width][height];
            Gy = new double[width][height];
            G  = new double[width][height];
            for (int i=0; i<width; i++) {
                for (int j=0; j<height; j++) {
                    if (i==0 || i>=width-2 || j==0 || j==height-1)
                        Gx[i][j] = Gy[i][j] = G[i][j] = 0;
                    else{
                        Gx[i][j] = output[i+1][j-1] + 2*output[i+1][j] + output[i+1][j+1] -
                                output[i-1][j-1] - 2*output[i-1][j] - output[i-1][j+1];
                        Gy[i][j] = output[i-1][j+1] + 2*output[i][j+1] + output[i+1][j+1] -
                                output[i-1][j-1] - 2*output[i][j-1] - output[i+1][j-1];
                        G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
                    }
                }
            }
            counter = 0;
            for(int ii = 0 ; ii < width ; ii++ )
            {
                for(int jj = 0 ; jj < height ; jj++ )
                {
                    //pixels[counter] = (int) G[ii][jj];
                    int temp = (int)G[ii][jj];
                    int r = (temp>>16) & 0xff;
                    int g = (temp>>8) & 0xff;
                    int b = (temp) & 0xff;
                    temp = (r+g+b)/3;
                    bitmap.setPixel(ii,jj,temp);
                    counter = counter + 1;
                }
            }
        return bitmap;
    }


    public static Bitmap arrToBmp(double G[][],Bitmap bitmap){
        int counter = 0;
        for(int ii = 0 ; ii < bitmap.getWidth() ; ii++ )
        {
            for(int jj = 0 ; jj < bitmap.getHeight() ; jj++ )
            {
                //pixels[counter] = (int) G[ii][jj];
                bitmap.setPixel(ii,jj,(int) G[ii][jj]);
                counter = counter + 1;
            }
        }
        return bitmap;
    }


    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeFile(path, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;

        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        return BitmapFactory.decodeFile(path, options);
    }
    /**
     * Decode and sample down a bitmap from a byte stream
     */
    public static Bitmap decodeSampledBitmapFromByte(Context context, byte[] bitmapBytes) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int reqWidth, reqHeight;
        Point point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(point);
            reqWidth = point.x;
            reqHeight = point.y;
        } else {
            reqWidth = display.getWidth();
            reqHeight = display.getHeight();
        }


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Load & resize the image to be 1/inSampleSize dimensions
        // Use when you do not want to scale the image with a inSampleSize that is a power of 2
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false; // If set to true, the decoder will return null (no bitmap), but the out... fields will still be set, allowing the caller to query the bitmap without having to allocate the memory for its pixels.
        options.inPurgeable = true;         // Tell to gc that whether it needs free memory, the Bitmap can be cleared
        options.inInputShareable = true;    // Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future

        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height
     *
     * The function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);

        int roundedInSampleSize;
        if (initialInSampleSize <= 8) {
            roundedInSampleSize = 1;
            while (roundedInSampleSize < initialInSampleSize) {
                // Shift one bit to left
                roundedInSampleSize <<= 1;
            }
        } else {
            roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
        }

        return roundedInSampleSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final double height = options.outHeight;
        final double width = options.outWidth;

        final long maxNumOfPixels = reqWidth * reqHeight;
        final int minSideLength = Math.min(reqHeight, reqWidth);

        int lowerBound = (maxNumOfPixels < 0) ? 1 :
                (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 :
                (int) Math.min(Math.floor(width / minSideLength),
                        Math.floor(height / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap BmpForKNN(Context context, Bitmap bitmap, String direction){
        int cropHeight;
        if (bitmap.getHeight() > bitmap.getWidth()) cropHeight = bitmap.getWidth();
        else                                        cropHeight = bitmap.getHeight();

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropHeight, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        bitmap = getResizedBitmap(bitmap, Constants.width, Constants.height);
        // Sobel edge detector

        bitmap = applySobel(bitmap);
        return bitmap;
    }



}
