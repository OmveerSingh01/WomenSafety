package com.omveer.womensafety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 onboardingViewPager;
    private Button buttonNext;
    private List<OnboardingItem> onboardingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check if onboarding already completed
        SharedPreferences prefs = getSharedPreferences("onboarding", MODE_PRIVATE);
        boolean completed = prefs.getBoolean("completed", false);
        if (completed) {
            // Skip onboarding
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        buttonNext = findViewById(R.id.buttonNext);

        // Prepare slides
        onboardingItems = new ArrayList<>();
        onboardingItems.add(new OnboardingItem(R.drawable.welcome, "Welcome", "This app keeps you safe"));
        onboardingItems.add(new OnboardingItem(R.drawable.share_location, "Emergency", "Send SOS instantly"));
        onboardingItems.add(new OnboardingItem(R.drawable.sos_button, "Contacts", "Save your trusted contacts"));

        OnboardingAdapter adapter = new OnboardingAdapter(onboardingItems);
        onboardingViewPager.setAdapter(adapter);

        // Next button click
        buttonNext.setOnClickListener(v -> {
            int next = onboardingViewPager.getCurrentItem() + 1;
            if (next < onboardingItems.size()) {
                onboardingViewPager.setCurrentItem(next);
            } else {
                // ✅ Mark onboarding completed
                prefs.edit().putBoolean("completed", true).apply();

                // Go to MainActivity
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                finish();
            }
        });

        // Change button text on last page
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == onboardingItems.size() - 1) {
                    buttonNext.setText("Get Started");
                } else {
                    buttonNext.setText("Next");
                }
            }
        });
    }
}
