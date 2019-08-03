package tsa.medangomedical;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * Popup part of ManageSymptoms class
 * Add new symptoms
 */
public class AddSymptomPopup extends Activity implements AdapterView.OnItemSelectedListener {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner symptoms;
    private EditText otherInfo;
    private Button symDate;
    private boolean dateNotSelected = true, symNotSelected = true, otherReq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_symptom_popup);

        otherInfo = (EditText) findViewById(R.id.addSymInfo);
        symDate = (Button) findViewById(R.id.symptomDate);
        Button addSymBtn = (Button) findViewById(R.id.popSymBtn);

        //creates a smaller window for the popup
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        symptoms = (Spinner) findViewById(R.id.symType);
        ArrayAdapter<CharSequence> symAdapter = ArrayAdapter.createFromResource(this, R.array.sym, android.R.layout.simple_spinner_item);
        symAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        symptoms.setAdapter(symAdapter);
        symptoms.setOnItemSelectedListener(this);

        /*
         * Executes when "Select Start Date" button is clicked
         * Creates a time picker dialog
         */
        symDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddSymptomPopup.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });
        // Executes when user clicks "Ok" on the time picker dialog box
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = month + "/" + dayOfMonth + "/" + year;
                symDate.setText(date);
                dateNotSelected = false;
            }
        };

        // Get inputs and write to memory
        addSymBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symptomInput = symptoms.getSelectedItem().toString().trim();
                symptomInput = symptomInput.replace(" (specify below)", "");
                String otherInfoInput = otherInfo.getText().toString().toLowerCase().trim();
                String dateInput = symDate.getText().toString().toLowerCase().trim();

                if (symNotSelected || dateNotSelected || (otherReq && otherInfoInput.isEmpty()))
                    makeToast();
                else {
                    if (!(otherInfoInput.isEmpty()))
                        otherInfoInput += "\n";
                    SharedPreferences sPref = getSharedPreferences("newSym", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sPref.edit();
                    editor.putString("symName", symptomInput);
                    editor.putString("symInfo", otherInfoInput);
                    editor.putString("symDate", dateInput);
                    editor.apply();

                    clearInputs();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        symNotSelected = symptoms.getItemAtPosition(position).toString().equals("[ Symptom ]");
        otherReq = symptoms.getSelectedItem().toString().trim().contains("specify");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Set all input fields to empty/default
     */
    public void clearInputs() {
        String ssd = "Select Start Date";
        symptoms.setSelection(0);
        otherInfo.setText("");
        symDate.setText(ssd);
        dateNotSelected = true;
    }

    public void makeToast() {
        Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_LONG).show();
    }
}