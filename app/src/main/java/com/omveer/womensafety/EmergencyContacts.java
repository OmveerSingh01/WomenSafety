package com.omveer.womensafety;

public class EmergencyContacts {
    public String name1, phone1, name2, phone2, name3, phone3;

    public EmergencyContacts() {} // Required for Firebase

    public EmergencyContacts(String name1, String phone1,
                             String name2, String phone2,
                             String name3, String phone3) {
        this.name1 = name1;
        this.phone1 = phone1;
        this.name2 = name2;
        this.phone2 = phone2;
        this.name3 = name3;
        this.phone3 = phone3;
    }
}
