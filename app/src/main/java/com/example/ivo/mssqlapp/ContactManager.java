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
        if(contacts == null){
            contacts = new ArrayList<Contact>();

            for(int i = 0; i < firstNameArray.length; i++){
                Contact contact = new Contact();
                contact.firstName = firstNameArray[i];
                contact.lastName = lastNameArray[i];
                contacts.add(contact);
            }
        }

        return contacts;
    }
}
