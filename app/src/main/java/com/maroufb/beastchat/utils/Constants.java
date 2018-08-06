package com.maroufb.beastchat.utils;

import com.maroufb.beastchat.Entities.User;

import java.util.HashMap;

public class Constants {

    public static final String IP_LOCAL_HOST = "http://172.16.1.34:4000";

    public static final String USER_INFO_PREFERENCE = "USER_INFO_PREFERENCE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PICTURE = "USER_PICTURE";


    public final static String GOOGLE_ID = "466090969946-roaajj8hs9bqor93amc0ah7pch4695f3.apps.googleusercontent.com";


    public static final int GOOGLE_SIGN_IN = 9001;

    public static final String FIRE_BASE_PATH_USERS = "users";

    public static final String FIREBASE_PATH_FRIEND_REQUEST_SENT = "friendRequestsSent";

    public static final String FIREBASE_PATH_FRIEND_REQUEST_RECEIVED = "friendRequestsReceived";

    public static final String FIREBASE_PATH_USER_FRIENDS = "userFriends";

    public static final String FIREBASE_PATH_USER_TOKEN = "userToken";

    public static final String FIREBASE_PATH_USER_MESSAGES = "userMessages";

    public static final String FIREBASE_PATH_USER_NEW_MESSAGES = "userNewMessages";

    public static String encodeEmail(String email){
        return email.replace(".",",");
    }

    public static boolean isIncludedInMap(HashMap<String, User> userHashMap, User user){
        return  user!= null && userHashMap.size() != 0
                && userHashMap.containsKey(user.getEmail()) ;
    }



}
