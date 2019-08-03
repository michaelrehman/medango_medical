package tsa.medangomedical;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

/**
 * Main screen
 * Links all the other activities together
 */
public class Welcome extends AppCompatActivity {

    private AlertDialog toBeImplemented;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        toBeImplemented = new AlertDialog.Builder(this).create();
        toBeImplemented.setCanceledOnTouchOutside(false);
        toBeImplemented.setCancelable(false);
        toBeImplemented.setTitle("Messages is not yet implemented.");
        toBeImplemented.setMessage("Messages will have the function to send strange/new symptoms to their doctor.");
        toBeImplemented.setButton(DialogInterface.BUTTON_POSITIVE, "CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toBeImplemented.dismiss();
            }
        });
    }

    public void goToManageContacts(View v) {
        this.startActivity(new Intent(this, ManageContacts.class));
    }

    public void goToMessages(View v) {
        toBeImplemented.show();
    }

    public void goToMedicationsSymptoms(View v){
        this.startActivity(new Intent(this, MedicationsSymptoms.class));
    }

    public void goToCalendar(View v) {
        this.startActivity(new Intent(this, Calendar.class));
    }

    public void goToSettings(View v) {
        this.startActivity(new Intent(this, Settings.class));
    }
}