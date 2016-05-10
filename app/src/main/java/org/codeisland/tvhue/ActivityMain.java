package org.codeisland.tvhue;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import org.codeisland.tvhue.dialog.BridgeListDialog;
import org.codeisland.tvhue.dialog.PushLinkDialog;
import org.codeisland.tvhue.utils.HueHelper;
import org.codeisland.tvhue.utils.Pref;

import java.util.List;

public class ActivityMain extends AppCompatActivity implements PHSDKListener {

    public static final String LOG_TAG = "TvHue";

    @BindView(R.id.connection_status) TextView connectionStatus;
    @BindView(R.id.hue_ip) TextView hueIp;
    @BindView(R.id.selected_brightness) SeekBar brightnessSeeker;
    @BindView(R.id.selected_scene) Spinner sceneSpinner;

    private PHHueSDK hueSdk;
    private Dialog pushLinkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scr_main);
        ButterKnife.bind(this);

        // Setup bridge:
        startHue();
    }

    @OnClick(R.id.reconnect_hue)
    public void reconnectHue(){
        connectionStatus.setText(R.string.status_searching);
        searchHue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHue();
    }

    // ----------------- HUE SDK ----------------------

    private void startHue(){
        hueSdk = PHHueSDK.getInstance();
        // Set app-info:
        hueSdk.setAppName("TvHue");
        hueSdk.setDeviceName(Build.MODEL);
        // register listener:
        hueSdk.getNotificationManager().registerSDKListener(this);

        if (Pref.hasStoredBridge(this)) {
            // Connect to stored bridge!
            connectionStatus.setText(R.string.status_connecting);
            PHAccessPoint bridge = Pref.loadBridge(this);
            connectHue(bridge);
        } else {
            // Search a bridge
            connectionStatus.setText(R.string.status_searching);
            searchHue();
        }
    }

    private void stopHue(){
        hueSdk.getNotificationManager().unregisterSDKListener(this);
        hueSdk.disableAllHeartbeat();
    }

    private void searchHue(){
        PHBridgeSearchManager search = (PHBridgeSearchManager) hueSdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        search.search(true, true); // Notifies onAccessPointFound in listener above!
    }

    private void connectHue(PHAccessPoint bridge){
        if (!hueSdk.isAccessPointConnected(bridge)) {
            // Otherwise, this would thrown an exception -.-
            hueSdk.connect(bridge);
        }
    }

    @Override
    public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {
        // Cache was updated. Why would I need this?
    }

    @Override
    public void onBridgeConnected(final PHBridge bridge, String username) {
        // Store connection for next-time!
        Pref.storeBridge(this, bridge, username);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (pushLinkDialog != null && pushLinkDialog.isShowing()){
                    pushLinkDialog.dismiss();
                }
                connectionStatus.setText(R.string.status_connected);
                hueIp.setText(HueHelper.getIp(bridge));
            }
        });
        // Start heart-beat
        hueSdk.setSelectedBridge(bridge);
        hueSdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        // Display indicator to press the push-button now!
        hueSdk.startPushlinkAuthentication(phAccessPoint);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(R.string.status_authenticating);
                pushLinkDialog = PushLinkDialog.display(ActivityMain.this);
            }
        });
    }

    @Override
    public void onAccessPointsFound(final List<PHAccessPoint> accessPoints) {
        if (accessPoints.size() == 1){
            connectHue(accessPoints.get(0));
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BridgeListDialog.showList(ActivityMain.this, accessPoints, new BridgeListDialog.BridgeSelectedListener() {
                        @Override
                        public void bridgeSelected(PHAccessPoint bridge) {
                            connectHue(bridge);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onError(int code, String message) {
        // Errors like wrong parameter or no authentication
        Log.e(LOG_TAG, "Error code " + code + ": " + message);
        switch (code){
            case PHMessageType.PUSHLINK_AUTHENTICATION_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pushLinkDialog != null && pushLinkDialog.isShowing()){
                            pushLinkDialog.dismiss();
                        }
                    }
                });
                break;
        }
        // TODO Loacal Braodcast, depending on error
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(R.string.status_error);
            }
        });
    }

    @Override
    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        // Connection to bridge lost. Notify user!
        Log.d(LOG_TAG, "Connection Lost!");
        // TODO Local broadcast
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(R.string.status_connection_lost);
            }
        });
    }

    @Override
    public void onConnectionResumed(PHBridge phBridge) {
        // Connection found again. This seems to be called every 10 seconds...
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(R.string.status_connected);
            }
        });
    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> list) {
        // If there where any internal JSON parsing errors. Shouldn't happen!
        Log.e(LOG_TAG, "Parsing error!" + list.toString());
        // TODO Local Broadcast
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(R.string.status_error);
            }
        });
    }

}
