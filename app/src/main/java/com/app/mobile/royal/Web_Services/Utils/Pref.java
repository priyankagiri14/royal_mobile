package com.app.mobile.royal.Web_Services.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Pref {
    public static final String USER_TOKEN = "user_token";
    public static final String ID = "ID";
    public static final String BATCH_ID = "id";
    public static final String BATCH_NO = "batchNo";
    public static final String BATCH_ARRAY = "batcharray";
    public static final String ALLOCATION_ID = "allocation_id";
    public static final String CITY = "city";
    public static final String FNAME = "firstName";
    public static final String USER_ID = "USER_ID";
    public static final String SUBURB ="suburb";
    public static final String ADDRESS ="address";
    public static final String POSTAL_CODE ="postal_code";

    public static final String IS_RICA = "isRica";
    public static SharedPreferences getPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //put username no in shared pref
    public static void putToken(Context context,String token)
    {
        getPref(context).edit().putString(USER_TOKEN,token).commit();
    }
    public static String getUserToken(Context context) {
        return getPref(context).getString(USER_TOKEN, null);
    }

    public static void putId(Context context,String Id)
    {
        getPref(context).edit().putString(ID,Id).commit();
    }
    public static String getId(Context context) {
        return getPref(context).getString(ID, null);
    }

    public static void putAllocationId(Context context,String Id)
    {
        getPref(context).edit().putString(ALLOCATION_ID,Id).commit();
    }
    public static String getAllocationId(Context context) {
        return getPref(context).getString(ALLOCATION_ID, null);
    }

    public static void setBatchNo(Context context,String batch)
    {
        getPref(context).edit().putString(BATCH_NO,batch).commit();
    }
    public static String getBatchNo(Context context) {
        return getPref(context).getString(BATCH_NO, null);
    }
    public static void setBatchID(Context context,String id)
    {
        getPref(context).edit().putString(BATCH_ID,id).commit();
    }
    public static String getBatchID(Context context) {
        return getPref(context).getString(BATCH_ID, null);
    }

    // for suburb
    public static void setSuburb(Context context,String suburb)
    {
        getPref(context).edit().putString(SUBURB,suburb).commit();
    }
    public static String getSuburb(Context context) {
        return getPref(context).getString(SUBURB, null);
    }


    // for address
    public static void setAddress(Context context,String address)
    {
        getPref(context).edit().putString(ADDRESS,address).commit();
    }
    public static String getAddress(Context context) {
        return getPref(context).getString(ADDRESS, null);
    }

    // for address
    public static void setPostalCode(Context context,String postal_code)
    {
        getPref(context).edit().putString(POSTAL_CODE,postal_code).commit();
    }
    public static String getPostalCode(Context context) {
        return getPref(context).getString(POSTAL_CODE, null);
    }


    // for rica value
    public static void setIsRica(Context context,String isRica)
    {
        getPref(context).edit().putString(IS_RICA,isRica).commit();
    }
    public static String getIsRica(Context context) {
        return getPref(context).getString(IS_RICA, "false");
    }
    public static void removeIsRica(Context context)
    {
        getPref(context).edit().remove(IS_RICA).commit();
    }

    // for city

    public static void setCity(Context context,String city)
    {
        getPref(context).edit().putString(CITY,city).commit();
    }
    public static String getCity(Context context) {
        return getPref(context).getString(CITY, null);
    }

    public static void removeCity(Context context)
    {
        getPref(context).edit().remove(CITY).commit();
    }

    public static void removeAddress(Context context)
    {
        getPref(context).edit().remove(ADDRESS).commit();
    }

    public static void removePostalCode(Context context)
    {
        getPref(context).edit().remove(POSTAL_CODE).commit();
    }

    public static void removeSuburb(Context context)
    {
        getPref(context).edit().remove(SUBURB).commit();
    }

    public static void putBatchArray(Context context,String batcharray)
    {
        getPref(context).edit().putString(BATCH_ARRAY,batcharray).commit();
    }
    public static String getBatchArray(Context context) {
        return getPref(context).getString(BATCH_ARRAY, null);
    }

    public static void putFirstName(Context context,String firstname)
    {
        getPref(context).edit().putString(FNAME,firstname).commit();
    }
    public static String getFirstName(Context context) {
        return getPref(context).getString(FNAME, null);
    }

    public static void putUserId(Context context,String userId)
    {
        getPref(context).edit().putString(USER_ID,userId).commit();
    }
    public static String getUserId(Context context) {
        return getPref(context).getString(USER_ID, null);
    }
}