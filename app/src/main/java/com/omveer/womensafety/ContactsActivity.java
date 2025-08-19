package com.omveer.womensafety;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactsActivity extends AppCompatActivity {

    private EditText name1EditText, phone1EditText,
            name2EditText, phone2EditText,
            name3EditText, phone3EditText;
    private Button saveButton;
    private TextView savedContactsTextView;

    private DatabaseReference contactsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        auth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize UI elements
        name1EditText = findViewById(R.id.name1EditText);
        phone1EditText = findViewById(R.id.phone1EditText);
        name2EditText = findViewById(R.id.name2EditText);
        phone2EditText = findViewById(R.id.phone2EditText);
        name3EditText = findViewById(R.id.name3EditText);
        phone3EditText = findViewById(R.id.phone3EditText);
        saveButton = findViewById(R.id.saveButton);
        savedContactsTextView = findViewById(R.id.savedContactsTextView);

        String userId = auth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance()
                .getReference("contacts")
                .child(userId);

        // Load existing contacts safely
        contactsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                EmergencyContacts contacts = snapshot.getValue(EmergencyContacts.class);
                if (contacts != null) {
                    name1EditText.setText(contacts.name1 != null ? contacts.name1 : "");
                    phone1EditText.setText(contacts.phone1 != null ? contacts.phone1 : "");
                    name2EditText.setText(contacts.name2 != null ? contacts.name2 : "");
                    phone2EditText.setText(contacts.phone2 != null ? contacts.phone2 : "");
                    name3EditText.setText(contacts.name3 != null ? contacts.name3 : "");
                    phone3EditText.setText(contacts.phone3 != null ? contacts.phone3 : "");
                    updateSavedContactsTextView(contacts);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load contacts", Toast.LENGTH_SHORT).show()
        );

        saveButton.setOnClickListener(v -> saveContacts());
    }

    private void saveContacts() {
        String name1 = name1EditText.getText().toString().trim();
        String phone1 = phone1EditText.getText().toString().trim();
        String name2 = name2EditText.getText().toString().trim();
        String phone2 = phone2EditText.getText().toString().trim();
        String name3 = name3EditText.getText().toString().trim();
        String phone3 = phone3EditText.getText().toString().trim();

        if ((name1.isEmpty() && phone1.isEmpty()) &&
                (name2.isEmpty() && phone2.isEmpty()) &&
                (name3.isEmpty() && phone3.isEmpty())) {
            Toast.makeText(this, "Enter at least one contact", Toast.LENGTH_SHORT).show();
            return;
        }

        EmergencyContacts contacts = new EmergencyContacts(name1, phone1, name2, phone2, name3, phone3);
        contactsRef.setValue(contacts)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contacts saved successfully", Toast.LENGTH_SHORT).show();
                    updateSavedContactsTextView(contacts);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save contacts", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateSavedContactsTextView(EmergencyContacts contacts) {
        StringBuilder builder = new StringBuilder();
        if (contacts.name1 != null && !contacts.name1.isEmpty() || contacts.phone1 != null && !contacts.phone1.isEmpty()) {
            builder.append("Name: ").append(contacts.name1 != null ? contacts.name1 : "").append("\n");
            builder.append("Phone: ").append(contacts.phone1 != null ? contacts.phone1 : "").append("\n\n");
        }
        if (contacts.name2 != null && !contacts.name2.isEmpty() || contacts.phone2 != null && !contacts.phone2.isEmpty()) {
            builder.append("Name: ").append(contacts.name2 != null ? contacts.name2 : "").append("\n");
            builder.append("Phone: ").append(contacts.phone2 != null ? contacts.phone2 : "").append("\n\n");
        }
        if (contacts.name3 != null && !contacts.name3.isEmpty() || contacts.phone3 != null && !contacts.phone3.isEmpty()) {
            builder.append("Name: ").append(contacts.name3 != null ? contacts.name3 : "").append("\n");
            builder.append("Phone: ").append(contacts.phone3 != null ? contacts.phone3 : "").append("\n\n");
        }
        savedContactsTextView.setText(builder.toString());
    }
}
