package tsa.medangomedical;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/** Searches for the user-specified doctor in the database */
public class FindDoctor extends AsyncTask<String, Void, String> {

    AlertDialog dialog;
    Context context;

    public FindDoctor(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() { }

    @Override
    protected void onPostExecute(String s) { }

    private Object HttpURLConnection;

    @Override
    public String doInBackground(String... voids) {
        String result = "";
        String firstName = voids[0].toLowerCase();
        String lastName = voids[1].toLowerCase();
        String doctorId = voids[2];

        String conn = "http://104.14.170.76:8080/read_from_doctors.php";

        try {
            URL url = new URL(conn);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
            String data = URLEncoder.encode("firstName", "UTF-8") + "=" + URLEncoder.encode(firstName, "UTF-8") +
                    "&&" + URLEncoder.encode("lastName", "UTF-8") + "=" + URLEncoder.encode(lastName, "UTF-8") + "&&" +
                    URLEncoder.encode("doctorId", "UTF-8") + "=" + URLEncoder.encode(doctorId, "UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line;
            }

            reader.close();
            ips.close();
            http.disconnect();
            return result;


        }
        catch (MalformedURLException e) {
            result = e.getMessage();
        }
        catch (IOException e) {
            result = e.getMessage();
        }

        return result;
    }
}