package beothorn.github.com.toroidalgo.publisher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogHandler {

    public boolean confirm(Activity activity, String title, String confirmText, String okBtn, String cancelBtn, final Runnable okProcedure, final Runnable cancelProcedure) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setMessage(confirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        okProcedure.run();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        cancelProcedure.run();
                    }
                });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
    }

}
