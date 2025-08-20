package com.omveer.womensafety;

import android.os.Bundle;
import android.widget.TextView;  // Importd TextView
import android.text.method.LinkMovementMethod; //  Importd for clickable links
import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        TextView aboutAppText = findViewById(R.id.aboutAppText);
        // Enable clickable links inside the TextView
        aboutAppText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
