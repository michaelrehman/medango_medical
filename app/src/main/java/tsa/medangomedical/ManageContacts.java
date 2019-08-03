package tsa.medangomedical;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manage list of contacts
 * TODO remove/edit contacts
 */
public class ManageContacts extends AppCompatActivity {

    // Used to store/display data
    private ArrayList<String> nameRelation, contactInfo;
    private SimpleAdapter nameRelationInfoAdapter;
    private ListView contactsDisplay;
    private LinkedHashMap<String, String> nameRelationInfo;
    private List<HashMap<String, String>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_contacts);

        Button addCon = (Button) findViewById(R.id.popupCon);
        contactsDisplay = (ListView) findViewById(R.id.conList);
        nameRelation = new ArrayList<String>();
        contactInfo = new ArrayList<String>();

        addCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vAddSym) {
                startActivity(new Intent(getBaseContext(), AddContactPopup.class));
            }
        });

        populateListView();
    }

    // Gets called right after onCreate() so no reason to put this code there as well
    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }

    /**
     * Populate the ListView with values
     */
    public void populateListView() {
        loadListView();
        nameRelationInfo = new LinkedHashMap<String, String>();
        for (int i = 0; i < nameRelation.size(); i++)
            nameRelationInfo.put(nameRelation.get(i), contactInfo.get(i));
        listItems = new ArrayList<>();

        nameRelationInfoAdapter = new SimpleAdapter(this, listItems, R.layout.list_display_edit,
                new String[]{"First Line", "Second Line"}, //Keys the list items will look for to later populate the ListView
                new int[]{R.id.listItemMain, R.id.listItemSub}) {
            // Set onClickListener on buttons
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final String conName = ((TextView) view.findViewById(R.id.listItemMain)).getText().toString();

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

        Iterator it = nameRelationInfo.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        contactsDisplay.setAdapter(nameRelationInfoAdapter);
        saveListView();
    }

    public void updateListView() {
        listItems.clear();   //prevents duplication of displayed data
        addContact();
        for (int i = 0; i < nameRelation.size(); i++)
            nameRelationInfo.put(nameRelation.get(i), contactInfo.get(i));
        Iterator it = nameRelationInfo.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry) it.next();
            resultsMap.put("First Line", pair.getKey().toString());
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }
        nameRelationInfoAdapter.notifyDataSetChanged();
        saveListView();
    }

    private void addContact() {
        SharedPreferences sPref = getSharedPreferences("newContact", MODE_PRIVATE);
        if (!(sPref.getString("conName", "").isEmpty())) {
            nameRelation.add(sPref.getString("conName", "Example Name") + " (" + sPref.getString("conRelation", "Example Relation") + ")");
            contactInfo.add(sPref.getString("conPhone", "PhoneNumber") + " - " + sPref.getString("conEmail", "Email"));
        }
    }

    private void saveListView() {
        try {
            FileOutputStream output = openFileOutput("nameRelation.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(nameRelation.size());
            for (String line : nameRelation)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream output = openFileOutput("conInfo.txt", MODE_PRIVATE);
            DataOutputStream dout = new DataOutputStream(output);
            dout.writeInt(contactInfo.size());
            for (String line : contactInfo)
                dout.writeUTF(line);
            dout.flush();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListView() {
        try {
            FileInputStream input = openFileInput("nameRelation.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                nameRelation.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream input = openFileInput("conInfo.txt");
            DataInputStream din = new DataInputStream(input);
            int sz = din.readInt();
            for (int i = 0; i < sz; i++) {
                String line = din.readUTF();
                contactInfo.add(line);
            }
            din.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}