package com.omveer.womensafety;

public class OnboardingItem {
    private final int image;
    private final String title;
    private final String description;

    public OnboardingItem(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getImage() { return image; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
