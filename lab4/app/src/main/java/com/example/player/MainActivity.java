package com.example.player;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.ui.PlayerView;

@UnstableApi
public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private Uri selectedFileUri = null;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private DownloadHandler downloadHandler;
    private ExoPlayerMediaHandler mediaHandler;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonChooseFile = findViewById(R.id.buttonChooseFile);
        Button buttonPlay = findViewById(R.id.buttonPlay);
        Button buttonDownloadFile = findViewById(R.id.buttonDownloadFile);
        playerView = findViewById(R.id.player_view);

        // Initialize media handler with ExoPlayer
        mediaHandler = new ExoPlayerMediaHandler(this, playerView);

        downloadHandler = new DownloadHandler(this);
//        downloadHandler.setMediaHandler(mediaHandler);

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                        // Take persistable permissions for content URI
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(
                                selectedFileUri, takeFlags);

                        Log.d(TAG, "File selected: " + selectedFileUri);
                        mediaHandler.setMediaType(selectedFileUri);
                    }
                }
        );

        buttonChooseFile.setOnClickListener(v -> chooseFile());
        buttonPlay.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                mediaHandler.playMedia(selectedFileUri);
            } else {
                Log.d(TAG, "No file selected");
                showNoFileSelectedDialog();
            }
        });

        buttonDownloadFile.setOnClickListener(v -> showDownloadDialog());

        // Register receiver for download completion
        registerReceiver(downloadHandler.onDownloadComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED);
    }

    private void showNoFileSelectedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No File Selected")
                .setMessage("Please select a media file first")
                .setPositiveButton("Select File", (dialog, which) -> chooseFile())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDownloadDialog() {
        EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Enter URL")
                .setView(input)
                .setPositiveButton("Download", (dialog, which) -> {
                    String url = input.getText().toString();
                    downloadHandler.downloadFile(url);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showPlayDownloadedFileDialog(Uri fileUri) {
        new AlertDialog.Builder(this)
                .setTitle("Download Complete")
                .setMessage("Do you want to play the downloaded file?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    selectedFileUri = fileUri;
                    mediaHandler.playMedia(fileUri);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"audio/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        filePickerLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaHandler.release();
        unregisterReceiver(downloadHandler.onDownloadComplete);
    }
}