package tsa.medangomedical;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Set and view reminders for medications
 * TODO set up recurring reminders
 * TODO (maybe) link with database
 * TODO implement more permanent solution (refer to above)
 */
public class Calendar extends AppCompatActivity {

    private Button openDialog;
    private static final int DIALOG_ID = R.style.Theme_AppCompat_Light_Dialog_MinWidth;
    private int hour_x, minute_x;
    private String medName = "";   //used to specify which medication a reminder is for
    private ArrayList<String> meds, times;
    private SimpleAdapter medTimeAdapter;
    private ListView displayMeds;
    private LinkedHashMap<String, String> medDoseFreq;
    private List<HashMap<String, String>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        displayMeds = (ListView) findViewById(R.id.calendarMeds);
        meds = new ArrayList<String>();
        times = new ArrayList<String>();

        CalendarView calendarView = findViewById(R.id.calendar);
        //Executes when a new date is selected in the calender
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //TODO Change listview to display medications of that day
            }
        });

        populateListView();

        showTimePickerDialog();
    }

    /** Shows time picker dialog */
    public void showTimePickerDialog() {
        openDialog = findViewById(R.id.addReminder);
        openDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }

    /** Creates time picker dialog to show */
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new TimePickerDialog(this, timePickerListener, hour_x, minute_x, false);
        return null;
    }

    /** Executes when user sets the time py pressing "Ok" on time picker dialog */
    protected TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            Intent intent = new Intent(getBaseContext(), ChooseMedication.class);
            startActivityForResult(intent, 1);
        }
    };

    /**
     * When a medication is selected in ChooseMedication.java,
     * set the medName variable to the name of the medication selected.
     * If a medication was not selected,
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            medName = data.getStringExtra("medName");
        }
        else
            medName = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        //only if a medication has been selected
        if (!(medName.isEmpty())) {
            meds.add(medName);

            String amPM = " AM";
            //convert 24hr to 12hr format
            if (hour_x > 12) {   //13hr-23hr is 12hr + 12
                hour_x -= 12;
                amPM = " PM";   //because past noon
            }
            else if (hour_x == 12)
                amPM = " PM";   //because noon
            else if (hour_x == 0)  //00hr is midnight in 12hr
                hour_x = 12;
            String hour = "" + hour_x;

            String minute = "" + minute_x;
            if (minute_x < 10)
                minute = "0" + minute_x;

            times.add(hour + ":" + minute + amPM);
        }
    }

    public void populateListView() {
        medDoseFreq = new LinkedHashMap<String, String>();
        for (int i = 0; i < meds.size(); i++)
            medDoseFreq.put(meds.get(i), times.get(i));
        listItems = new ArrayList<>();

        medTimeAdapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.list_display,
                new String[]{"First Line", "Second Line"}, //Keys the list items will look for to later populate the ListView
                new int[]{R.id.listItemMain, R.id.listItemSub});

        Iterator it = medDoseFreq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        displayMeds.setAdapter(medTimeAdapter);
    }

    public void updateListView() {
        listItems.clear();
//        getData();

    }
}