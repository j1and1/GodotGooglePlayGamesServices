package com.jandans.godotgoogleplaygamesserviceslib;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

public class GooglePlayGamesServices extends GodotPlugin{

    public static String TAG = "GodotPlayServices";
    private GoogleSignInClient mGoogleSignInClient = null;
    private GoogleSignInAccount mSignedInAccount = null;

    public GooglePlayGamesServices(Godot godot) {
        super(godot);
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GooglePlayGamesServices";
    }

    public void initialisePlayServices()
    {
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    }

    public void signOut()
    {
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

    private void handleException(Exception ex, String description)
    {

    }

    private void onDisconnected()
    {

    }
}
