package com.prilojenie.wally;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WallpaperService extends Service {

    private static final String TAG = "WallpaperService";
    public static final String EXTRA_FOLDER_URI = "folderUri";
    private static final String PREFS_NAME = "WallpaperSwitcherPrefs";
    private static final String PREFS_KEY_LAST_IMAGE = "lastImage";

    private Handler handler;
    private List<String> imageUris = new ArrayList<>();
    private int currentImageIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(EXTRA_FOLDER_URI)) {
            String folderUri = intent.getStringExtra(EXTRA_FOLDER_URI);
            loadImagesFromFolder(folderUri);
            startSwitchingWallpapers();
        }
        return START_STICKY;
    }

    private void loadImagesFromFolder(String folderUri) {
        Uri uri = Uri.parse(folderUri);
        File folder = new File(uri.getPath());
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                String filePath = file.getAbsolutePath();
                if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".png")) {
                    imageUris.add(Uri.fromFile(file).toString());
                }
            }
        }
    }

    private void startSwitchingWallpapers() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastImageUri = prefs.getString(PREFS_KEY_LAST_IMAGE, null);
        if (lastImageUri != null && imageUris.contains(lastImageUri)) {
            currentImageIndex = imageUris.indexOf(lastImageUri);
        }
        switchWallpaper();
    }

    private void switchWallpaper() {
        if (imageUris.size() > 0) {
            String imageUri = imageUris.get(currentImageIndex);
            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setDataAndType(Uri.parse(imageUri), "image/");
            intent.putExtra("mimeType", "image/");
            WallpaperService.this.startActivity(Intent.createChooser(intent, "Set as"));
            currentImageIndex++;
            if (currentImageIndex >= imageUris.size()) {
                currentImageIndex = 0;
            }
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(PREFS_KEY_LAST_IMAGE, imageUri);
            editor.apply();
            handler.postDelayed(this::switchWallpaper, 600000); // switch wallpaper every 10 minutes
        } else {
            Log.e(TAG, "No images found in folder");
        }
    }@Override
    public IBinder onBind(Intent intent) {
        return null;
    }}