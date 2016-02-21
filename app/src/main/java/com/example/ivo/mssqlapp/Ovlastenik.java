package com.example.ivo.mssqlapp;

import java.io.Serializable;

/**
 * Created by Ivo on 26.8.2015..
 */
public class Ovlastenik implements Serializable{
    private int Id;
    private String Naziv;

    public Ovlastenik(int id, String naziv){
        this.Id = id;
        this.Naziv = naziv;
    }

    public int getId() {
        return Id;
    }

    public String getNaziv() {
        return Naziv;
    }
}