package org.codeisland.tvhue.utils;

import com.philips.lighting.model.PHBridge;

/**
 * Created by lukas on 10.05.16.
 */
public class HueHelper {

    private HueHelper(){}

    public static String getIp(PHBridge bridge){
        return bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
    }
}
