package tsa.medangomedical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Popup part of ManageMedications class
 * Add new medications
 */
public class AddMedicationPopup extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText medicine, dosageNum, freqNum;
    private Spinner dosageUnits, freqType;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_medication_popup);

        Button addMedBtn = (Button) findViewById(R.id.addMedBtn);
        medicine = (EditText) findViewById(R.id.medName);
        dosageNum = (EditText) findViewById(R.id.doseInput);
        freqNum = (EditText) findViewById(R.id.freqInput);

        //Changes activity dimensions to appear as a popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        //Fill spinners with Strings
        dosageUnits = (Spinner) findViewById(R.id.doseUnits);
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dosageUnits.setAdapter(unitAdapter);
        dosageUnits.setOnItemSelectedListener(this);
        //Next spinner
        freqType = (Spinner) findViewById(R.id.freq);
        ArrayAdapter<CharSequence> freqAdapter = ArrayAdapter.createFromResource(this, R.array.freqCalendar, android.R.layout.simple_spinner_item);
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqType.setAdapter(freqAdapter);
        freqType.setOnItemSelectedListener(this);

        // Get inputs and write
        addMedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medInput = medicine.getText().toString().trim();
                String doseNumInput = dosageNum.getText().toString().toLowerCase().trim();
                String doseUnitsInput = dosageUnits.getSelectedItem().toString().toLowerCase().trim();
                String freqNumInput = freqNum.getText().toString().toLowerCase().trim();
                String freqTypeInput = freqType.getSelectedItem().toString().toLowerCase().trim();

                if (medInput.isEmpty() || doseNumInput.isEmpty() || doseUnitsInput.isEmpty()
                        || freqNumInput.isEmpty() || freqTypeInput.isEmpty())
                    makeToast();
                else {
                    SharedPreferences sPref = getSharedPreferences("newMed", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("medName", medInput);
                    editor.putString("medDose", doseNumInput + doseUnitsInput);
                    editor.putString("medFreq", freqNumInput + " " + freqTypeInput);
                    editor.apply();

                    clearInputs();
                }
            }
        });
    }

    /**
     * Executed when an item is selected in the spinner
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * Executed if nothing is selected in the spinner
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Set all input fields to empty/default
     */
    public void clearInputs() {
        medicine.setText("");
        dosageNum.setText("");
        freqNum.setText("");
        dosageUnits.setSelection(0);
        freqType.setSelection(0);
    }

    public void makeToast() {
        Toast.makeText(this, "Fill all fields.", Toast.LENGTH_LONG).show();
    }
}