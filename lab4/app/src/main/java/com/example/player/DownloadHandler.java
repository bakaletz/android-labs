package com.example.player;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.media3.common.util.UnstableApi;

import java.io.File;

@UnstableApi
public class DownloadHandler {
    private final Context context;
    private long downloadID;
    private ExoPlayerMediaHandler mediaHandler;
    private static final String TAG = "DownloadHandler";

    public DownloadHandler(Context context) {
        this.context = context;
    }

    // Add reference to MediaHandler
    public void setMediaHandler(ExoPlayerMediaHandler mediaHandler) {
        this.mediaHandler = mediaHandler;
    }

    public void downloadFile(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            // Extract filename from URL
            String fileName = url.substring(url.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) fileName = "downloaded_file";

            request.setTitle("Downloading " + fileName);
            request.setDescription("Please wait...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Set destination with actual filename
            File destinationDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (destinationDir != null) {
                request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);

                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (manager != null) {
                    downloadID = manager.enqueue(request);
                    Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Download started: " + url);
                } else {
                    Toast.makeText(context, "Download manager not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Storage not available", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "Invalid URL format", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Download error: " + e.getMessage());
        }
    }

    public final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Log.d(TAG, "Download complete, ID: " + id);

                // Get downloaded file URI
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                if (manager == null) {
                    Toast.makeText(context, "Could not access download", Toast.LENGTH_SHORT).show();
                    return;
                }

                try (Cursor cursor = manager.query(query)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (statusIndex != -1 && cursor.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) {
                            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            if (uriIndex != -1) {
                                String uriString = cursor.getString(uriIndex);
                                Uri downloadedUri = Uri.parse(uriString);

                                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Downloaded file URI: " + downloadedUri);

                                // Now we need to handle the downloaded file
                                if (mediaHandler != null) {
                                    boolean isVideo = mediaHandler.setMediaType(downloadedUri);
                                    Log.d(TAG, "File type detected as video: " + isVideo);

                                    // Ask user if they want to play the file now
                                    showPlayDownloadedFileDialog(downloadedUri);
                                } else {
                                    Log.e(TAG, "MediaHandler is null, can't play downloaded file");
                                }
                            }
                        } else {
                            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing download: " + e.getMessage());
                    Toast.makeText(context, "Error processing download", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void showPlayDownloadedFileDialog(Uri fileUri) {
        if (context instanceof MainActivity) {
            ((MainActivity) context).showPlayDownloadedFileDialog(fileUri);
        }
    }
}
