/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhihu.matisse.internal.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.fragment.app.Fragment;

import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaStoreCompat {

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private CaptureStrategy mCaptureStrategy;
    private Uri mCurrentPhotoUri;
    private String mCurrentPhotoPath;

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void setCaptureStrategy(CaptureStrategy strategy) {
        mCaptureStrategy = strategy;
    }

    public void dispatchCaptureIntent(Context context, String action, int requestCode) {
        Intent captureIntent = new Intent(action);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
            File file = null;
            try {
                file = isCaptureVideo(action) ? createVideoFile() : createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("SingleMediaScanner", "scan finish!");

            if (file != null) {
                mCurrentPhotoPath = file.getAbsolutePath();
                mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(),
                        mCaptureStrategy.authority, file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    List<ResolveInfo> resInfoList = context.getPackageManager()
                            .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, mCurrentPhotoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }
                if (mFragment != null) {
                    mFragment.get().startActivityForResult(captureIntent, requestCode);
                } else {
                    mContext.get().startActivityForResult(captureIntent, requestCode);
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("IMG_%s.jpg", timeStamp);
        File storageDir;
        if (mCaptureStrategy.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists())
                storageDir.mkdirs();
        } else {
            storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (mCaptureStrategy.directory != null) {
            storageDir = new File(storageDir, mCaptureStrategy.directory);
            if (!storageDir.exists())
                storageDir.mkdirs();
        }

        // Avoid joining path components manually
        File tempFile = new File(storageDir, imageFileName);

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createVideoFile() throws IOException {
        // Create an video file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = String.format("VID_%s.mp4", timeStamp);
        File storageDir;
        if (mCaptureStrategy.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists())
                storageDir.mkdirs();
        } else {
            storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (mCaptureStrategy.directory != null) {
            storageDir = new File(storageDir, mCaptureStrategy.directory);
            if (!storageDir.exists())
                storageDir.mkdirs();
        }

        // Avoid joining path components manually
        File tempFile = new File(storageDir, videoFileName);

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    private boolean isCaptureVideo(String action) {
        return MediaStore.ACTION_VIDEO_CAPTURE.equalsIgnoreCase(action);
    }

    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}
