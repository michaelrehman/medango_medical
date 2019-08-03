package tsa.medangomedical;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/** Specify doctor and change phone number, email, or street address */
public class Settings extends AppCompatActivity {

    private Button b_changeDoctor;
    private EditText et_phone, et_email, et_address;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        b_changeDoctor = (Button) findViewById(R.id.b_changeDoctor);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_address = (EditText) findViewById(R.id.et_address);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                name = data.getStringExtra("docName");
                b_changeDoctor.setText(name);
            }
        }
    }

    public void openDoctorDialog(View view){
        Intent intent = new Intent(this, DoctorPopup.class);
        this.startActivityForResult(intent, 2);
    }


    public void goToNotifications(View view){
        Intent intent = new Intent(this, Notifications.class);
        this.startActivity(intent);
    }
}