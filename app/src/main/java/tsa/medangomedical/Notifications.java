package tsa.medangomedical;

import android.app.TimePickerDialog;
import android.icu.util.TimeZone;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.util.Calendar;

/** Customize notifications for medication reminders */
public class Notifications extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private RadioGroup vibes, sounds;
    private RadioButton rb;
    private MediaPlayer mp;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifications);
        vibes = findViewById(R.id.rg_vibes);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sounds = findViewById(R.id.rg_sounds);
    }

    /** Exectute vibration when an option is selected */
    public void checkBtnVibes(View view) {
        int radioId = vibes.getCheckedRadioButtonId();
        rb = findViewById(radioId);
        if (rb.getText().toString().equals("3 one-second long vibrations")) {
            vibrator.vibrate(1000);
        }
        else if (rb.getText().toString().equals("1 three-second long vibration")) {
            vibrator.vibrate(3000);
        }
        else {
            vibrator.vibrate(500);
        }
    }

    /**
     * PLay sound when option is selected
     * "raw" is a folder containing the .mp3 files
     */
    public void checkBtnSounds(View view){
        int radioId = sounds.getCheckedRadioButtonId();
        rb = findViewById(radioId);
        if(rb.getText().toString().equals("Option 1")){
            mp = MediaPlayer.create(this, R.raw.open_ended);
        }
        else if(rb.getText().toString().equals("Option 2")){
            mp = MediaPlayer.create(this, R.raw.plucky);
        }
        else{
            mp = MediaPlayer.create(this, R.raw.quite_impressed);
        }
        mp.start();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
    }

    public void finish(View view){
        finish();
    }
}