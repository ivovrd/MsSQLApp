package com.example.ivo.mssqlapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 1.7.2015..
 */
public class ContactManager {
    private static String[] firstNameArray = {"John", "Peter", "Harold", "Jeremy", "Christian"};
    private static String[] lastNameArray = {"Doe", "Robertson", "Finch", "Clarkson", "Walker"};

    private static ContactManager mInstance;
    private List<Contact> contacts;

    public static ContactManager getInstance(){
        if(mInstance == null){
            mInstance = new ContactManager();
        }

        return mInstance;
    }

    public List<Contact> getContacts(){

        return contacts;
    }

    public void setContacts(String firstName, String lastName, String eMail, String phoneNum){
        if(contacts == null) {
            contacts = new ArrayList<Contact>();
        }

        Contact contact = new Contact();
        contact.firstName = firstName;
        contact.lastName = lastName;
        contact.eMail = eMail;
        contact.phoneNumber = phoneNum;

        contacts.add(contact);
    }
}