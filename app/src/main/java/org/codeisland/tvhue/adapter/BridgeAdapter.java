package org.codeisland.tvhue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import org.codeisland.tvhue.R;

import java.util.List;

/**
 * Created by Lukas on 30.04.2016.
 */
public class BridgeAdapter extends ArrayAdapter<PHAccessPoint> {

    private final LayoutInflater inflater;

    public BridgeAdapter(Context context, List<PHAccessPoint> bridges) {
        super(context, -1, bridges);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View recycle, ViewGroup parent) {
        View v = recycle;
        if (v == null){
            v = this.inflater.inflate(R.layout.item_bridge, parent, false);
            TextView id = (TextView) v.findViewById(R.id.item_bridge_id);
            TextView ip = (TextView) v.findViewById(R.id.item_bridge_ip);
            v.setTag(new Holder(id, ip));
        }
        Holder holder = (Holder) v.getTag();
        PHAccessPoint bridge = getItem(position);
        holder.id.setText(bridge.getBridgeId());
        holder.ip.setText(bridge.getIpAddress());
        return v;
    }

    private class Holder{
        private final TextView id;
        private final TextView ip;

        public Holder(TextView id, TextView ip) {
            this.id = id;
            this.ip = ip;
        }
    }
}
