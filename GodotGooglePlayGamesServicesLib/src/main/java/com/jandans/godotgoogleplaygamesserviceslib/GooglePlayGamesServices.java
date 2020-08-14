package com.jandans.godotgoogleplaygamesserviceslib;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GooglePlayGamesServices extends GodotPlugin {

    private static final String TAG = "GodotPlayServices";
    private GoogleSignInClient mGoogleSignInClient = null;

    // Client variables
    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    //private EventsClient mEventsClient;
    //private PlayersClient mPlayersClient;
    private GoogleSignInAccount mLoggedInAccount;

    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    //Godot signal related variables
    private SignalInfo mOnConnectedSignal = new SignalInfo("onConnected");
    private SignalInfo mOnDisonnectedSignal = new SignalInfo("onDisconnected");

    public GooglePlayGamesServices(Godot godot) {
        super(godot);
    }

    // Export name, methods and signals to godot
    @NonNull
    @Override
    public String getPluginName() {
        return "GooglePlayGamesServices";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return new ArrayList<String>() {
            {
                add("initialise");
                add("isSignedIn");
                add("signInSilently");
                add("startSignInIntent");
                add("signOut");
                add("showAchievements");
                add("showLeaderboards");
                add("reportScore");
                add("unlockAchievement");
                add("incrementAchievement");
                add("getAccountInfo");
            }
        };
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        return new HashSet<SignalInfo>() {
            {
                add(mOnConnectedSignal);
                add(mOnDisonnectedSignal);
            }
        };
    }
    // END: Export name, methods and signals to godot

    public void initialise(boolean requestProfile, boolean requestEmail) {
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        if(requestProfile) {
            builder.requestProfile();
        }
        if (requestEmail) {
            builder.requestEmail();
        }
        GoogleSignInOptions options = builder.build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), options);
    }

    public boolean isSignedIn() {
        return mLoggedInAccount != null;
    }

    public String getAccountInfo()
    {
        if (isSignedIn())
        {
            return Helpers.AccountToJSON(mLoggedInAccount);
        }
        else
        {
            return "";
        }
    }

    public void signInSilently() {
        if (mGoogleSignInClient == null) {
            Log.e(TAG, "Please call initialise()");
            return;
        }
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(getActivity(),
            new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInSilently(): success");
                        onConnected(task.getResult());
                    } else {
                        Log.e(TAG, "signInSilently(): failure", task.getException());
                        startSignInIntent();
                    }
                }
            });
    }

    public void startSignInIntent() {
        if (mGoogleSignInClient == null) {
            Log.e(TAG, "Please call initialise()");
            return;
        }
        getActivity().startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    public void signOut() {
        if (mGoogleSignInClient == null) {
            Log.e(TAG, "Please call initialise()");
            return;
        }

        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signOut(): success");
                    } else {
                        handleException(task.getException(), "signOut() failed!");
                    }

                    onDisconnected();
                }
            });
    }

    public void showAchievements() {
        if (mAchievementsClient == null) {
            Log.e(TAG, "User might not be signed in.");
            return;
        }
        mAchievementsClient.getAchievementsIntent()
            .addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    getActivity().startActivityForResult(intent, RC_UNUSED);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    handleException(e, "AchievementException");
                }
            });
    }

    public void showLeaderboards() {
        if (mLeaderboardsClient == null) {
            Log.e(TAG, "User might not be signed in.");
            return;
        }
        mLeaderboardsClient.getAllLeaderboardsIntent()
            .addOnSuccessListener(new OnSuccessListener<Intent>() {
                @Override
                public void onSuccess(Intent intent) {
                    getActivity().startActivityForResult(intent, RC_UNUSED);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    handleException(e, "LeaderboardException");
                }
            });
    }

    public void reportScore(String leaderboardID, int score) {
        if (mLeaderboardsClient == null) {
            Log.e(TAG, "User might not be signed in.");
            return;
        }
        mLeaderboardsClient.submitScore(leaderboardID, score);
    }

    public void unlockAchievement(String achievementID) {
        if (mAchievementsClient == null) {
            Log.e(TAG, "User might not be signed in.");
            return;
        }
        mAchievementsClient.unlock(achievementID);
    }

    public void incrementAchievement(String achievementID, int steps) {
        if (mAchievementsClient == null) {
            Log.e(TAG, "User might not be signed in.");
            return;
        }
        mAchievementsClient.increment(achievementID, steps);
    }

    private void handleException(Exception e, String description) {
        int status = 0;

        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = String.format("Call failed with error code: %d and message: %s", status, description);
        Log.e(TAG, message, e);
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onMainActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = "Sign in error";
                }

                onDisconnected();
                handleException(apiException, message);
            }
        }
    }

    private void onConnected(GoogleSignInAccount resultingAccount)
    {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mLoggedInAccount = resultingAccount;
        mAchievementsClient = Games.getAchievementsClient(getActivity(), resultingAccount);
        mLeaderboardsClient = Games.getLeaderboardsClient(getActivity(), resultingAccount);
        //mEventsClient = Games.getEventsClient(getActivity(), resultingAccount);
        //mPlayersClient = Games.getPlayersClient(getActivity(), resultingAccount);

        emitSignal(mOnConnectedSignal.getName());
    }

    private void onDisconnected()
    {
        Log.d(TAG, "onDisconnected()");

        mAchievementsClient = null;
        mLeaderboardsClient = null;
        //mPlayersClient = null;
        //mEventsClient = null;
        mLoggedInAccount = null;

        emitSignal(mOnDisonnectedSignal.getName());
    }
}
