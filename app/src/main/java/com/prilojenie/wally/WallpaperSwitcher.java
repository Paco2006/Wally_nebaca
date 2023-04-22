package com.prilojenie.wally;

import static android.app.Activity.RESULT_OK;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

public class WallpaperSwitcher extends Fragment {
    private static final int PICK_FOLDER_REQUEST_CODE = 1;
    private Button pickFolderButton;
    Activity activity;
    View parentHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = getActivity();
        parentHolder = inflater.inflate(R.layout.wallpaper_switcher, container, false);
        super.onCreate(savedInstanceState);
        pickFolderButton = activity.findViewById(R.id.select_folder_button);
//        pickFolderButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
//            }
//        });
        return parentHolder;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FOLDER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                String folderUri = data.getData().toString();
                startWallpaperService(folderUri);
            }
        }
    }

    private void startWallpaperService(String folderUri) {
        Intent intent = new Intent(activity, WallpaperService.class);
        intent.putExtra(WallpaperService.EXTRA_FOLDER_URI, folderUri);
        activity.startService(intent);
    }
}

