package tsa.medangomedical;

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
import android.widget.AdapterView;
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
 * Manage list of symptoms
 * TODO remove/edit symptoms
 */
public class ManageSymptoms extends Fragment {

    // Used to store/display data
    private ArrayList<String> symArrayList, otherDateArrayList;
    private SimpleAdapter symOtherDateAdapter;
    private ListView display;
    private LinkedHashMap<String, String> symOtherDate;
    private List<HashMap<String, String>> listItems;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View msView = inflater.inflate(R.layout.fragment_manage_symptoms, container, false);

        Button addSym = (Button) msView.findViewById(R.id.popupSym);
        display = (ListView) msView.findViewById(R.id.symList);
        symArrayList = new ArrayList<String>();
        otherDateArrayList = new ArrayList<String>();

        addSym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vAddSym) {
                startActivity(new Intent(getActivity(), AddSymptomPopup.class));
            }
        });

        populateListView();

        return msView;
    }

    //Gets called right after onCreate() so no reason to put this code there as well
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
        symOtherDate = new LinkedHashMap<String, String>();
        for (int i = 0; i < symArrayList.size(); i++)
            symOtherDate.put(symArrayList.get(i), otherDateArrayList.get(i));
        listItems = new ArrayList<>();

        symOtherDateAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.list_display_edit,
                new String[]{"First Line", "Second Line"}, //Keys the list items will look for to later populate the ListView
                new int[]{R.id.listItemMain, R.id.listItemSub}) {
            //Set onClickListener on buttons
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);

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

                saveListView();
                return view;
            }
        };

        Iterator it = symOtherDate.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        display.setAdapter(symOtherDateAdapter);
    }

    public void updateListView() {
        listItems.clear();   //prevents duplication of displayed data
        addSym();
        for (int i = 0; i < symArrayList.size(); i++)
            symOtherDate.put(symArrayList.get(i), otherDateArrayList.get(i));
        Iterator it = symOtherDate.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        symOtherDateAdapter.notifyDataSetChanged();
        saveListView();
    }

    private void addSym() {
        SharedPreferences sPref = this.getActivity().getSharedPreferences("newSym", MODE_PRIVATE);
        if (!(sPref.getString("symName", "").isEmpty())) {
            symArrayList.add(sPref.getString("symName", ""));
            otherDateArrayList.add(sPref.getString("symInfo", "") + sPref.getString("symDate", ""));
        }
    }

    private void saveListView() {
        try {
            FileOutputStream output = getActivity().openFileOutput("symArrList.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(symArrayList.size());
            for (String line : symArrayList)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream output = getActivity().openFileOutput("otherDateArrList.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(otherDateArrayList.size());
            for (String line : otherDateArrayList)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        try {
            FileInputStream input = getActivity().openFileInput("symArrList.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                symArrayList.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream input = getActivity().openFileInput("otherDateArrList.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                otherDateArrayList.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}