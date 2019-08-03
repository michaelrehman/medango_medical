package tsa.medangomedical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Choose medication to add a reminder for */
public class ChooseMedication extends Activity {

    private InputStream is;
    private String result = "";
    private ArrayList<String> medArrayList, doseFreqArrayList;
    private Intent iReturn;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_medication);

        //Checks if user has an internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else
            connected = false;

        iReturn = new Intent();

        //Allow network connection
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        //Changes activity dimensions to appear as a popup
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width*.7), (int)(height*.6));

        //Sets the position of the popup
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        final ListView displayMeds = (ListView) findViewById(R.id.chooseMedList);
        medArrayList = new ArrayList<String>();
        doseFreqArrayList = new ArrayList<String>();

        getData();

        //Begin populate ListView code
        final LinkedHashMap<String, String> medDoseFreq = new LinkedHashMap<String, String>();
        for (int i = 0; i < medArrayList.size(); i++)
            medDoseFreq.put(medArrayList.get(i), doseFreqArrayList.get(i));
        List<HashMap<String, String>> listItems = new ArrayList<>();

        SimpleAdapter medDoseFreqAdapter = new SimpleAdapter(this, listItems, R.layout.list_display,
                new String[] {"First Line", "Second Line"}, //Keys the list items will look for to later populate the ListView
                new int[] {R.id.listItemMain, R.id.listItemSub});

        Iterator it = medDoseFreq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }
        displayMeds.setAdapter(medDoseFreqAdapter);
        //End populate ListView code

        displayMeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if an item is selected, pass selected medication name to Calendar.java
                iReturn.putExtra("medName", medArrayList.get(position));
                setResult(RESULT_OK, iReturn);
                finish();
            }
        });
    }

    /**
     * Get data from MySQL medications table,
     * put it into a parsable String,
     * and put it into ArrayLists
     */
    private void getData() {
        String php = "http://104.14.170.76:8080/read_from_medications.php";
        //Gets the data
        try {
            URL url = new URL(php);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            is = new BufferedInputStream(http.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Put data from InputStream into a String
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "ISO_8859_1"));
            StringBuilder sb = new StringBuilder();
            String temp;   //to avoid calling br.readLine() twice
            while ((temp = br.readLine()) != null) {
                sb.append(temp + "\n");
            }
            is.close();
            result = sb.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Parse data from JSON file from read_from_medications.php
        try {
            if (!(result.isEmpty()) && connected) {
                JSONArray ja = new JSONArray(result);
                JSONObject jo;
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    medArrayList.add(jo.getString("medicine"));
                    doseFreqArrayList.add(jo.getString("dosage") + " - " + jo.getString("frequency"));
                }
            }
            else {
                final AlertDialog noConnection = new AlertDialog.Builder(this).create();
                noConnection.setCanceledOnTouchOutside(false);
                noConnection.setTitle("Connection Error");
                noConnection.setMessage("Unable to retrieve medications.");
                noConnection.setButton(DialogInterface.BUTTON_POSITIVE, "CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noConnection.dismiss();
                        finish();
                    }
                });
                noConnection.show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}