package org.codeisland.tvhue.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import org.codeisland.tvhue.R;

/**
 * Created by Lukas on 30.04.2016.
 */
public class PushLinkDialog {

    public static Dialog display(Context context){
        ImageView instructions = new ImageView(context);
        instructions.setImageResource(R.drawable.pushlink_image);

        return new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_pushlink_title)
                .setMessage(R.string.dialog_pushlink_message)
                .setView(instructions)
                .create();
    }

}
