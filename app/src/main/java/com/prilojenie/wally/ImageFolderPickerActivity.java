package com.prilojenie.wally;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImageFolderPickerActivity extends AppCompatActivity {

    private static final int PICK_FOLDER_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "WallpaperSwitcherPrefs";
    private static final String PREFS_KEY_FOLDER_URI = "folderUri";
    private TextView folderPathTextView;
    private Button selectFolderButton;
    private Button startSwitchingButton;
    private String selectedFolderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_folder_picker);

        folderPathTextView = findViewById(R.id.folder_path_text_view);
        selectFolderButton = findViewById(R.id.select_folder_button);
        startSwitchingButton = findViewById(R.id.start_switching_button);

        selectFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
            }
        });

        startSwitchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFolderPath != null) {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    prefs.edit().putString(PREFS_KEY_FOLDER_URI, selectedFolderPath).apply();

                    Intent intent = new Intent(ImageFolderPickerActivity.this, WallpaperService.class);
                    intent.putExtra(WallpaperService.EXTRA_FOLDER_URI, selectedFolderPath);
                    startService(intent);

                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FOLDER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            selectedFolderPath = getPathFromUri(uri);
            folderPathTextView.setText(selectedFolderPath);
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String[] parts = documentId.split(":");
                if (parts.length > 1) {
                    String type = parts[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + parts[1];
                    }
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = Uri.parse("content://downloads/public_downloads");
                Uri downloadUri = Uri.withAppendedPath(contentUri, documentId);
                path = getDataColumn(downloadUri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        String filePath = null;
        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                filePath = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }
}