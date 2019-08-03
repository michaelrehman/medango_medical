package tsa.medangomedical;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A part of the MedicationsSymptoms class
 * Manage list of medications
 * TODO remove/edit medications
 */
public class ManageMedications extends Fragment {

    // Used to store/display data
    private ArrayList<String> medArrayList, doseFreqArrayList;
    private SimpleAdapter medDoseFreqAdapter;
    private ListView display;
    private LinkedHashMap<String, String> medDoseFreq;
    private List<HashMap<String, String>> listItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mmView = inflater.inflate(R.layout.fragment_manage_medications, container, false);

        Button showPopup_btn = (Button) mmView.findViewById(R.id.popupMed);
        display = (ListView) mmView.findViewById(R.id.medList);
        medArrayList = new ArrayList<String>();
        doseFreqArrayList = new ArrayList<String>();

        showPopup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddMedicationPopup.class));
            }
        });

        populateListView();

        return mmView;
    }

    /**
     * Update the ListView when returning to it from AddMedicaitonPopup.java
     * Gets called right after onCreate() so no reason to put this code there as well
     */
    @Override
    public void onResume() {
        super.onResume();
        updateListView();
    }

    /**
     * Populate the ListView with values
     */
    public void populateListView() {
        loadListView();
        medDoseFreq = new LinkedHashMap<String, String>();
        for (int i = 0; i < medArrayList.size(); i++)
            medDoseFreq.put(medArrayList.get(i), doseFreqArrayList.get(i));
        listItems = new ArrayList<>();

        medDoseFreqAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.list_display_edit,
                new String[]{"First Line", "Second Line"}, //Keys the list items will look for to later populate the ListView
                new int[]{R.id.listItemMain, R.id.listItemSub}) {
            //Set onClickListener on buttons
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final String medName = ((TextView) view.findViewById(R.id.listItemMain)).getText().toString();

                Button removeBtn = (Button) view.findViewById(R.id.removeBtn);
                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                Button editBtn = (Button) view.findViewById(R.id.editBtn);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                return view;
            }
        };

        Iterator it = medDoseFreq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        display.setAdapter(medDoseFreqAdapter);
        saveListView();
    }

    /**
     * Updates the data of the ListView (variable listItems)
     * Called in onResume() in case a new medication was added in the popup
     * Used instead of populateListView() since the adapter only needs to be created once
     */
    public void updateListView() {
        listItems.clear();   //prevents duplication of displayed data
        addMed();
        for (int i = 0; i < medArrayList.size(); i++)
            medDoseFreq.put(medArrayList.get(i), doseFreqArrayList.get(i));
        Iterator it = medDoseFreq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);   //this is the data set of the adapter
        }

        medDoseFreqAdapter.notifyDataSetChanged();
        saveListView();
    }

    private void addMed() {
        SharedPreferences sPref = this.getActivity().getSharedPreferences("newMed", MODE_PRIVATE);
        if (!(sPref.getString("medName", "").isEmpty())) {
            medArrayList.add(sPref.getString("medName", ""));
            doseFreqArrayList.add(sPref.getString("medDose", "") + " - " + sPref.getString("medFreq", ""));
        }
    }

    private void saveListView() {
        try {
            FileOutputStream output = getActivity().openFileOutput("medArrList.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(medArrayList.size());
            for (String line : medArrayList)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream output = getActivity().openFileOutput("doseFreqArrList.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(doseFreqArrayList.size());
            for (String line : doseFreqArrayList)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        try {
            FileInputStream input = getActivity().openFileInput("medArrList.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                medArrayList.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream input = getActivity().openFileInput("doseFreqArrList.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                doseFreqArrayList.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}