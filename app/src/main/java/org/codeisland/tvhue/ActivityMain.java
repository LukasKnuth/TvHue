package org.codeisland.tvhue;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import org.codeisland.tvhue.dialog.BridgeListDialog;
import org.codeisland.tvhue.dialog.PushLinkDialog;
import org.codeisland.tvhue.persistant.Pref;

import java.util.List;

public class ActivityMain extends AppCompatActivity implements PHSDKListener {

    private PHHueSDK hueSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scr_main);

        hueSdk = PHHueSDK.getInstance();
        // Set app-info:
        hueSdk.setAppName("TvHue");
        hueSdk.setDeviceName(Build.MODEL);
        // register listener:
        hueSdk.getNotificationManager().registerSDKListener(this);

        // Search a bridge
        PHBridgeSearchManager search = (PHBridgeSearchManager) hueSdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        search.search(true, true); // Notifies onAccessPointFound in listener above!
    }

    // ----------------- HUE SDK ----------------------

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        // Cache was updated. Why would I need this?
    }

    @Override
    public void onBridgeConnected(PHBridge phBridge, String username) {
        // Store connection for next-time!
        Pref.storeBridge(this, phBridge, username);
        // Start heart-beat
        hueSdk.setSelectedBridge(phBridge);
        hueSdk.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        // Display indicator to press the push-button now!
        hueSdk.startPushlinkAuthentication(phAccessPoint);
        PushLinkDialog.display(this);
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> list) {
        if (list.size() == 1){
            // TODO connect automatically
        } else {
            BridgeListDialog.showList(this, list, new BridgeListDialog.BridgeSelectedListener() {
                @Override
                public void bridgeSelected(PHAccessPoint bridge) {
                    // TODO connect!
                }
            });
        }
    }

    @Override
    public void onError(int i, String s) {
        // Errors like wrong parameter or no authentication
        // TODO Loacal Braodcast, depending on error
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        // Connection to bridge lost. Notify user!
        // TODO Local broadcast
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        // Connection found again. Only after onConnectionLost() ????
        // TODO Send local broadcast
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        // If there where any internal JSON parsing errors. Shouldn't happen!
        // TODO Local Broadcast
    }

}
