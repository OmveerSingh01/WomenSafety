package com.omveer.womensafety;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        TextView tipsTextView = findViewById(R.id.tipsTextView);
        tipsTextView.setText(
                "1. Always stay aware of your surroundings.\n\n" +
                        "2. Avoid walking alone at night.\n\n" +
                        "3. Keep your phone fully charged.\n\n" +
                        "4. Share your location with trusted contacts.\n\n" +
                        "5. Trust your instincts and avoid risky areas.\n\n" +
                        "6. Keep emergency numbers saved and easily accessible.\n\n" +
                        "7. Use public places and well-lit routes whenever possible.\n\n" +
                        "8. Inform family or friends about your whereabouts."
        );
    }
}
