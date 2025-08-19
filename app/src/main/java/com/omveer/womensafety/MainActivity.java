package com.omveer.womensafety;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnSos, btnLogin, btnContacts, btnTips;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        btnSos = findViewById(R.id.btnSos);
        btnLogin = findViewById(R.id.btnLogin);
        btnContacts = findViewById(R.id.btnContacts);
        btnTips = findViewById(R.id.btnTips);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending SOS...");
        progressDialog.setCancelable(false);

        btnLogin.setText("Logout");
        btnLogin.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        btnContacts.setOnClickListener(v ->
                startActivity(new Intent(this, ContactsActivity.class)));

        btnTips.setOnClickListener(v ->
                startActivity(new Intent(this, TipsActivity.class)));

        btnSos.setOnClickListener(v -> triggerSos());
    }

    private void triggerSos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS
            }, 1);
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference contactsRef = FirebaseDatabase.getInstance()
                .getReference("contacts")
                .child(userId);

        contactsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                EmergencyContacts contacts = snapshot.getValue(EmergencyContacts.class);
                if (contacts != null &&
                        ((contacts.phone1 != null && !contacts.phone1.isEmpty()) ||
                                (contacts.phone2 != null && !contacts.phone2.isEmpty()) ||
                                (contacts.phone3 != null && !contacts.phone3.isEmpty()))) {
                    sendSosMessage();
                } else {
                    Toast.makeText(this, "Please add contacts first", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please add contacts first", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch contacts", Toast.LENGTH_SHORT).show()
        );
    }

    private void sendSosMessage() {
        progressDialog.show();

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference contactsRef = FirebaseDatabase.getInstance()
                .getReference("contacts")
                .child(userId);

        contactsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                EmergencyContacts contacts = snapshot.getValue(EmergencyContacts.class);

                if (contacts != null) {
                    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        progressDialog.dismiss();

                        if (location != null) {
                            SmsManager smsManager = SmsManager.getDefault();
                            String locationLink = "https://maps.google.com/?q=" +
                                    location.getLatitude() + "," + location.getLongitude();

                            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = registerReceiver(null, ifilter);
                            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
                            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
                            int batteryPct = (int) ((level / (float) scale) * 100);

                            // Send messages to all 3 contacts if available
                            if (contacts.phone1 != null && !contacts.phone1.isEmpty()) {
                                String msg1 = (contacts.name1 != null && !contacts.name1.isEmpty() ? contacts.name1 : "Friend") +
                                        ", I am in danger! I need help. Battery: " + batteryPct + "%. My location: " + locationLink;
                                smsManager.sendTextMessage(contacts.phone1, null, msg1, null, null);
                            }

                            if (contacts.phone2 != null && !contacts.phone2.isEmpty()) {
                                String msg2 = (contacts.name2 != null && !contacts.name2.isEmpty() ? contacts.name2 : "Friend") +
                                        ", I am in danger! I need help. Battery: " + batteryPct + "%. My location: " + locationLink;
                                smsManager.sendTextMessage(contacts.phone2, null, msg2, null, null);
                            }

                            if (contacts.phone3 != null && !contacts.phone3.isEmpty()) {
                                String msg3 = (contacts.name3 != null && !contacts.name3.isEmpty() ? contacts.name3 : "Friend") +
                                        ", I am in danger! I need help. Battery: " + batteryPct + "%. My location: " + locationLink;
                                smsManager.sendTextMessage(contacts.phone3, null, msg3, null, null);
                            }

                            Toast.makeText(this, "SOS sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "No contacts found. Please add them first.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to get contacts", Toast.LENGTH_SHORT).show();
        });
    }
}
