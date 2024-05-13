package com.example.connection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.util.Log;

//import androidx.activity.EdgeToEdge;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.lv);
        new GetContacts().execute();
    }

    public class GetContacts extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void...arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://gist.githubusercontent.com/Baalmart/8414268/raw/43b0e25711472de37319d870cb4f4b35b1ec9d26/contacts";
            String jsonStr = sh.makeServiceCall(url);
            Log.e("Main", "Response from url:" + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.optString("office");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("address", address);
                        contact.put("gender", gender);
                        contact.put("mobile", mobile);
                        contact.put("home", home);
                        contact.put("office", office);

                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e("Main", "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "JSON parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e("Main", "Couldn't get JSON from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "couldn't get JSON from server. Check Logcat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.activity_main, new String[]{"id", "name", "email", "address", "gender", "mobile", "home", "office"}, new int[]{R.id.id, R.id.name, R.id.email, R.id.address, R.id.gender, R.id.mobile, R.id.home, R.id.office});
            lv.setAdapter(adapter);
        }
    }
}