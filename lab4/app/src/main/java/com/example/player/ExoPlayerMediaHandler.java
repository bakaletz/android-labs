package com.example.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

@UnstableApi
public class ExoPlayerMediaHandler {
    private final Context context;
    private final PlayerView playerView;
    private ExoPlayer player;
    private boolean isVideo = false;
    private static final String TAG = "ExoPlayerMediaHandler";

    public ExoPlayerMediaHandler(Context context, PlayerView playerView) {
        this.context = context;
        this.playerView = playerView;
        initializePlayer();
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        // Add a listener to detect when media is ready
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    // Check if current media has video track
                    isVideo = player.getVideoFormat() != null;
                    updatePlayerVisibility();
                }
            }
        });
    }

    public boolean setMediaType(Uri mediaUri) {
        if (mediaUri == null) return false;

        String type = context.getContentResolver().getType(mediaUri);
        Log.d(TAG, "Media type: " + type);

        if (type != null && type.startsWith("video")) {
            isVideo = true;
        } else if (type != null && type.startsWith("audio")) {
            isVideo = false;
        }
        return isVideo;
    }


    private void updatePlayerVisibility() {
        if (isVideo) {
            playerView.setVisibility(View.VISIBLE);
        } else {
            // For audio only, we can hide the video surface but keep controls visible
            playerView.setVisibility(View.VISIBLE);
            playerView.setUseController(true);
            playerView.getVideoSurfaceView().setVisibility(View.GONE);
        }
    }

    public void playMedia(Uri mediaUri) {
        if (mediaUri != null) {
            // Set the media item to play
            MediaItem mediaItem = MediaItem.fromUri(mediaUri);
            player.setMediaItem(mediaItem);

            // Prepare and play
            player.prepare();
            player.play();

            Log.d(TAG, "Started playing media: " + mediaUri);
        } else {
            Log.e(TAG, "Media URI is null");
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
            Log.d(TAG, "Released player resources");
        }
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public boolean getIsVideo() {
        return isVideo;
    }

    // For advanced features
    public ExoPlayer getPlayer() {
        return player;
    }
}