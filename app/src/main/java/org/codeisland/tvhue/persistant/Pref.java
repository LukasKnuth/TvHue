package org.codeisland.tvhue.persistant;

import android.content.Context;
import android.content.SharedPreferences;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.model.PHBridge;

/**
 * Created by lukas on 01.05.16.
 */
public class Pref {

    private static final String FILE_PREF = "hue_bridge_prefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_IP = "bridge-ip";
    
    private Pref(){}

    public static void storeBridge(Context context, PHBridge bridge, String username){
        SharedPreferences pref = getPref(context);
        pref.edit()
                .putString(PREF_IP, bridge.getResourceCache().getBridgeConfiguration().getIpAddress())
                .putString(PREF_USERNAME, username)
                .apply();
    }

    public static PHAccessPoint loadBridge(Context context){
        SharedPreferences pref = getPref(context);
        if (hasStoredBridge(context)) {
            PHAccessPoint bridge = new PHAccessPoint();
            bridge.setUsername(pref.getString(PREF_USERNAME, null));
            bridge.setIpAddress(pref.getString(PREF_IP, null));
            return bridge;
        } else {
            return null;
        }
    }

    public static boolean hasStoredBridge(Context context){
        SharedPreferences pref = getPref(context);
        return pref.contains(PREF_USERNAME) && pref.contains(PREF_IP);
    }

    private static SharedPreferences getPref(Context context){
        return context.getSharedPreferences(FILE_PREF, Context.MODE_PRIVATE);
    }

}
