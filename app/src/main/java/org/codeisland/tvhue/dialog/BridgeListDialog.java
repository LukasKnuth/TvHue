package org.codeisland.tvhue.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import org.codeisland.tvhue.R;
import org.codeisland.tvhue.adapter.BridgeAdapter;

import java.util.List;

/**
 * Created by Lukas on 30.04.2016.
 */
public class BridgeListDialog {

    public static Dialog showList(Context context, List<PHAccessPoint> bridges, final BridgeSelectedListener listener){
        final BridgeAdapter adapter = new BridgeAdapter(context, bridges);
        return new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_bridgeList_title)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PHAccessPoint bridge = adapter.getItem(which);
                        listener.bridgeSelected(bridge);
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public interface BridgeSelectedListener{
        public void bridgeSelected(PHAccessPoint bridge);
    }

}
