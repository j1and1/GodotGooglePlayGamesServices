package com.jandans.godotgoogleplaygamesserviceslib;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

public class Helpers {

    public static String AccountToJSON(GoogleSignInAccount account)
    {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", account.getId());
            jsonObject.put("displayName", account.getDisplayName());
            jsonObject.put("email", account.getEmail());
            jsonObject.put("photoUrl", account.getPhotoUrl());
            jsonObject.put("isExpired", account.isExpired());
            //TODO: add more fields in future if needed
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
