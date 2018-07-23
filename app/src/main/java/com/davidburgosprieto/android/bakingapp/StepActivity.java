package com.davidburgosprieto.android.bakingapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davidburgosprieto.android.bakingapp.data.RecipesContract;
import com.davidburgosprieto.android.bakingapp.utilities.LoadersUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class StepActivity extends AppCompatActivity
        implements ExoPlayer.EventListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = StepActivity.class.getSimpleName();

    public static final String RECIPE_ID_EXTRA = "RECIPE_ID_EXTRA";
    public static final String RECIPE_NAME_EXTRA = "RECIPE_NAME_EXTRA";
    public static final String STEP_ID_EXTRA = "STEP_ID_EXTRA";
    public static final String VIDEO_URL_EXTRA = "VIDEO_URL_EXTRA";
    public static final String SHORT_DESCRIPTION_EXTRA = "SHORT_DESCRIPTION_EXTRA";
    public static final String DESCRIPTION_EXTRA = "DESCRIPTION_EXTRA";
    public static final String STEPS_NUMBER_EXTRA = "STEPS_NUMBER_EXTRA";

    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;
    private NotificationManager mNotificationManager;
    private String mRecipeName, mVideoUrl, mShortDescription, mDescription;
    private int mRecipeId, mStepId, mStepsNumber;
    private TextView mDescriptionTextView;
    private TextView mNavigationIndicatorTextView;
    private TextView mNavigatorNext;
    private TextView mNavigatorPrevious;
    private RelativeLayout mNavigationLayout;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        // Butterknife doesn't work for some reason...
        mPlayerView = findViewById(R.id.step_video);
        mDescriptionTextView = findViewById(R.id.step_description);
        mNavigationIndicatorTextView = findViewById(R.id.step_navigation_indicator);
        mNavigatorNext = findViewById(R.id.step_navigation_next);
        mNavigatorPrevious = findViewById(R.id.step_navigation_previous);
        mNavigationLayout = findViewById(R.id.step_navigation_layout);

        // Get parameters from calling intent.
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mRecipeId = intent.getIntExtra(RECIPE_ID_EXTRA, 1);
            mRecipeName = intent.getStringExtra(RECIPE_NAME_EXTRA);
            mVideoUrl = intent.getStringExtra(VIDEO_URL_EXTRA);
            mShortDescription = intent.getStringExtra(SHORT_DESCRIPTION_EXTRA);
            mDescription = intent.getStringExtra(DESCRIPTION_EXTRA);
            mStepId = intent.getIntExtra(STEP_ID_EXTRA, 0);
            mStepsNumber = intent.getIntExtra(STEPS_NUMBER_EXTRA, 0);
        } else {
            mStepId = savedInstanceState.getInt(STEP_ID_EXTRA);
            mRecipeName = savedInstanceState.getString(RECIPE_NAME_EXTRA);
            mStepsNumber = savedInstanceState.getInt(STEPS_NUMBER_EXTRA);
        }

        getSupportLoaderManager().initLoader(LoadersUtils.ID_STEPS_LOADER, null, this);
    }

    void showStepData() {
        mCursor.moveToPosition(mStepId);
        mDescription = mCursor.getString(DetailActivity.INDEX_COLUMN_DESCRIPTION);
        mShortDescription = mCursor.getString(DetailActivity.INDEX_COLUMN_SHORT_DESCRIPTION);
        mVideoUrl = mCursor.getString(DetailActivity.INDEX_COLUMN_VIDEO_URL);
        mStepsNumber = mCursor.getCount();

        // Set text elements for describing current recipe step.
        setTitle(mShortDescription);
        mDescriptionTextView.setText(mRecipeName.toUpperCase() + "\n\n" + mDescription);
        //mNavigationIndicatorTextView.setText("Step " + mStepId + " of " + mStepsNumber);
        if (mStepId == 0)
            mNavigatorPrevious.setVisibility(View.GONE);
        else {
            mNavigatorPrevious.setVisibility(View.VISIBLE);
            mNavigatorPrevious.setText("Step " + (mStepId - 1));
        }
        if (mStepId == mStepsNumber - 1)
            mNavigatorNext.setVisibility(View.GONE);
        else {
            mNavigatorNext.setVisibility(View.VISIBLE);
            mNavigatorNext.setText("Step " + (mStepId + 1));
        }

        // Set video if available.
        RelativeLayout.LayoutParams layoutParams;
        if (mVideoUrl != null && !mVideoUrl.equals("")) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Initialize the Media Session.
            initializeMediaSession();

            // Initialize the player.
            initializePlayer(Uri.parse(mVideoUrl));
        } else {
            layoutParams = new RelativeLayout.LayoutParams(0, 0);
        }
        mPlayerView.setLayoutParams(layoutParams);
    }

    public void previousStep(View view) {
        stopVideo();
        mStepId--;
        showStepData();
    }

    public void nextStep(View view) {
        stopVideo();
        mStepId++;
        showStepData();
    }

    void stopVideo() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer = null;
        }
        if (mMediaSession != null)
            mMediaSession = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        stopVideo();
        outState.putInt(STEP_ID_EXTRA, mStepId);
        outState.putInt(STEPS_NUMBER_EXTRA, mStepsNumber);
        outState.putString(RECIPE_NAME_EXTRA, mRecipeName);
        outState.putString(VIDEO_URL_EXTRA, mVideoUrl);
        outState.putString(SHORT_DESCRIPTION_EXTRA, mShortDescription);
        outState.putString(DESCRIPTION_EXTRA, mDescription);
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        if (mMediaSession != null)
            return;

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Called when the timeline and/or manifest has been refreshed.
     * <p>
     * Note that if the timeline has changed then a position discontinuity may also have occurred.
     * For example the current period index may have changed as a result of periods being added or
     * removed from the timeline. The will <em>not</em> be reported via a separate call to
     * {@link #onPositionDiscontinuity()}.
     *
     * @param timeline The latest timeline. Never null, but may be empty.
     * @param manifest The latest manifest. May be null.
     */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    /**
     * Called when the available or selected tracks change.
     */
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    /**
     * Called when the player starts or stops loading the source.
     *
     * @param isLoading Whether the source is currently being loaded.
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Called when the value returned from either changes.
     *
     * @param playWhenReady Whether playback will proceed when ready.
     * @param playbackState One of the {@code STATE} constants defined in the {@link ExoPlayer}
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mMediaSession != null) {
            if (mStateBuilder != null) {
                if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                            mExoPlayer.getCurrentPosition(), 1f);
                } else if ((playbackState == ExoPlayer.STATE_READY)) {
                    mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            mExoPlayer.getCurrentPosition(), 1f);
                }

                mMediaSession.setPlaybackState(mStateBuilder.build());
                showNotification(mStateBuilder.build());
            }
        }
    }

    /**
     * Called when an error occurs.
     *
     * @param error The error.
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    /**
     * Called when a position discontinuity occurs without a change to the timeline. A position
     * discontinuity occurs when the current window or period index changes (as a result of playback
     * transitioning from one period in the timeline to the next), or when the playback position
     * jumps within the period currently being played (as a result of a seek being performed, or
     * when the source introduces a discontinuity internally).
     * <p>
     * When a position discontinuity occurs as a result of a change to the timeline this method is
     * <em>not</em> called. {@link #onTimelineChanged(Timeline, Object)} is called in this case.
     */
    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Shows Media Style notification, with actions that depend on the current MediaSession
     * PlaybackState.
     *
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, StepActivity.class), 0);

        builder.setContentTitle(mRecipeName)
                .setContentText(mShortDescription)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.baseline_local_dining_white_48)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersUtils.ID_STEPS_LOADER:
                Uri uri = RecipesContract.StepsEntry.CONTENT_URI;
                String[] selectionArgs = {Integer.toString(mRecipeId)};
                return new CursorLoader(StepActivity.this,
                        uri,
                        DetailActivity.MAIN_STEPS_PROJECTION,
                        RecipesContract.StepsEntry.COLUMN_RECIPE_ID + "=?",
                        selectionArgs,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        showStepData();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    @Override
    public void onBackPressed() {
        mExoPlayer.stop();
        setResult(RESULT_OK, getIntent());
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // Back button.
                onBackPressed();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
