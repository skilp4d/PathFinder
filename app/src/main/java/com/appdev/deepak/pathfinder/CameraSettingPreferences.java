package com.appdev.deepak.pathfinder;

/**
 * Created by 209de on 11/27/2015.
 */
import android.content.SharedPreferences;
import android.content.Context;
import android.hardware.Camera;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
/**
 * Created by 209de on 11/24/2015.
 */
public class CameraSettingPreferences {

    private static final String FLASH_MODE = "squarecamera__flash_mode";

    private static SharedPreferences getCameraSettingPreferences(@NonNull final Context context) {
        return context.getSharedPreferences("com.desmond.squarecamera", Context.MODE_PRIVATE);
    }

    protected static void saveCameraFlashMode(@NonNull final Context context, @NonNull final String cameraFlashMode) {
        final SharedPreferences preferences = getCameraSettingPreferences(context);

        if (preferences != null) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(FLASH_MODE, cameraFlashMode);
            editor.apply();
        }
    }

    protected static String getCameraFlashMode(@NonNull final Context context) {
        final SharedPreferences preferences = getCameraSettingPreferences(context);

        if (preferences != null) {
            return preferences.getString(FLASH_MODE, Camera.Parameters.FLASH_MODE_AUTO);
        }

        return Camera.Parameters.FLASH_MODE_AUTO;
    }

    /**
     * Created by 209de on 11/24/2015.
     */
    public static class ImageParameters implements Parcelable {

        public boolean mIsPortrait;

        public int mDisplayOrientation;
        public int mLayoutOrientation;

        public int mCoverHeight, mCoverWidth;
        public int mPreviewHeight, mPreviewWidth;

        public ImageParameters(Parcel in) {
            mIsPortrait = (in.readByte() == 1);

            mDisplayOrientation = in.readInt();
            mLayoutOrientation = in.readInt();

            mCoverHeight = in.readInt();
            mCoverWidth = in.readInt();
            mPreviewHeight = in.readInt();
            mPreviewWidth = in.readInt();
        }

        public ImageParameters() {}

        public int calculateCoverWidthHeight() {
            return Math.abs(mPreviewHeight - mPreviewWidth) / 2;
        }

        public int getAnimationParameter() {
            return mIsPortrait ? mCoverHeight : mCoverWidth;
        }

        public boolean isPortrait() {
            return mIsPortrait;
        }

        public ImageParameters createCopy() {
            ImageParameters imageParameters = new ImageParameters();

            imageParameters.mIsPortrait = mIsPortrait;
            imageParameters.mDisplayOrientation = mDisplayOrientation;
            imageParameters.mLayoutOrientation = mLayoutOrientation;

            imageParameters.mCoverHeight = mCoverHeight;
            imageParameters.mCoverWidth = mCoverWidth;
            imageParameters.mPreviewHeight = mPreviewHeight;
            imageParameters.mPreviewWidth = mPreviewWidth;

            return imageParameters;
        }

        public String getStringValues() {
            return "is Portrait: " + mIsPortrait + "," +
                    "\ncover height: " + mCoverHeight + " width: " + mCoverWidth
                    + "\npreview height: " + mPreviewHeight + " width: " + mPreviewWidth;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (mIsPortrait ? 1 : 0));

            dest.writeInt(mDisplayOrientation);
            dest.writeInt(mLayoutOrientation);

            dest.writeInt(mCoverHeight);
            dest.writeInt(mCoverWidth);
            dest.writeInt(mPreviewHeight);
            dest.writeInt(mPreviewWidth);
        }

        public static final Creator<ImageParameters> CREATOR = new Creator<ImageParameters>() {
            @Override
            public ImageParameters createFromParcel(Parcel source) {
                return new ImageParameters(source);
            }

            @Override
            public ImageParameters[] newArray(int size) {
                return new ImageParameters[size];
            }
        };
    }
}

