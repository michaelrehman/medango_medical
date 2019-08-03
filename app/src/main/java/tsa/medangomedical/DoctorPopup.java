package tsa.medangomedical;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/** Used to make changes to doctor */
public class DoctorPopup extends AppCompatActivity {

    private AlertDialog doctorFound;
    private Button b_submit;
    private EditText et_firstName, et_lastName, et_doctorId;
    private Intent iReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_popup);

        doctorFound = new AlertDialog.Builder(this).create();
        doctorFound.setCanceledOnTouchOutside(false);
        doctorFound.setCancelable(false);
        doctorFound.setTitle("Doctor Status");

        iReturn = getIntent();
        b_submit = (Button) findViewById(R.id.b_submit);
        et_firstName = (EditText) findViewById(R.id.et_firstName);
        et_lastName = (EditText) findViewById(R.id.et_lastName);
        et_doctorId = (EditText) findViewById(R.id.et_doctorId);

        b_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = et_firstName.getText().toString().trim();
                String lName = et_lastName.getText().toString().trim();
                String id = et_doctorId.getText().toString().trim();

                if (!(fName.isEmpty()) && !(lName.isEmpty()) && !(id.isEmpty())) {
                    checkDoctorTable(fName, lName, id);
                    iReturn.putExtra("docName", fName + " " + lName);
                    setResult(RESULT_OK, iReturn);
                }
                else
                    makeToast();
            }
        });

        //creates a smaller window for the popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }

    /** If the doctor is found, update the text of the button under Settings. */
    public void checkDoctorTable(String firstName, String lastName, String doctorId){
        FindDoctor fd = new FindDoctor(this);
        fd.execute(firstName, lastName, doctorId);
        try {
            String result = fd.get();
            if(result.equals("Doctor Found")) {
                doctorFound.setMessage(result);
                doctorFound.setButton(DialogInterface.BUTTON_POSITIVE, "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doctorFound.dismiss();
                        finish();
                    }
                });
                doctorFound.show();
            }
            else {
                doctorFound.setMessage(result);
                doctorFound.setButton(DialogInterface.BUTTON_POSITIVE, "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doctorFound.dismiss();
                    }
                });
                doctorFound.show();
            }
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void finishActivity(View view){
        finish();
    }

    public void makeToast() {
        Toast.makeText(this, "Fill all required fields", Toast.LENGTH_LONG).show();
    }
}