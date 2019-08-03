package tsa.medangomedical;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Popup part of ManageContacts class
 * Add new contacts
 */
public class AddContactPopup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinCon;
    private EditText name, phoneNum, email;
    private boolean relationNotSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_popup);

        Button addCon = (Button) findViewById(R.id.addConBtn);
        name = (EditText) findViewById(R.id.conName);
        phoneNum = (EditText) findViewById(R.id.conPhoneNum);
        email = (EditText) findViewById(R.id.conEmail);

        /*
         * Scales down the size of the popup window
         * Bases dimensions on current screen size
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.6));

        // Assigns string array to the "Relation" spinner
        spinCon = (Spinner) findViewById(R.id.relation);
        ArrayAdapter<CharSequence> conAdapter = ArrayAdapter.createFromResource(this, R.array.conRelation, android.R.layout.simple_spinner_item);
        conAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCon.setAdapter(conAdapter);
        spinCon.setOnItemSelectedListener(this);

        //  Get inputs and write to memory
        addCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameInput = name.getText().toString().trim();
                String phoneNumInput = phoneNum.getText().toString().toLowerCase().trim();
                String emailInput = email.getText().toString().toLowerCase().trim();
                String relationInput = spinCon.getSelectedItem().toString().toLowerCase().trim();

                if (nameInput.isEmpty() || phoneNumInput.isEmpty() || emailInput.isEmpty() || relationNotSelected)
                    makeToast();
                else {
                    SharedPreferences sPref = getSharedPreferences("newContact", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("conName", nameInput);
                    editor.putString("conPhone", phoneNumInput);
                    editor.putString("conEmail", emailInput);
                    editor.putString("conRelation", relationInput);
                    editor.apply();

                    clearInputs();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        relationNotSelected = spinCon.getItemAtPosition(position).toString().equals("[ Relationship ]");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /** Set all input fields to empty/default */
    public void clearInputs() {
        name.setText("");
        phoneNum.setText("");
        email.setText("");
        spinCon.setSelection(0);
        relationNotSelected = true;
    }

    public void makeToast() {
        Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_LONG).show();
    }
}