package com.example.ivo.mssqlapp;

import java.io.Serializable;

/**
 * Created by Ivo on 1.7.2015..
 */
public class DocPrev implements Serializable{
    private String sifra, datum, datumOd, datumDo, napomena, memo, ovlastenik;
    private int ovlastenikId, dani, radniDani, godina;

    public DocPrev(String sifra, String datum, String datumOd, String datumDo, int ovlastenikId, String napomena, String memo, String ovlastenik, int dani, int radniDani, int godina){
        this.sifra = sifra;
        this.datum = datum;
        this.datumOd = datumOd;
        this.datumDo = datumDo;
        this.ovlastenikId = ovlastenikId;
        this.napomena = napomena;
        this.memo = memo;
        this.ovlastenik = ovlastenik;
        this.dani = dani;
        this.radniDani = radniDani;
        this.godina = godina;
    }

    public String getSifra() {
        return sifra;
    }

    public String getDatum() {
        return datum;
    }

    public String getDatumOd() {
        return datumOd;
    }

    public String getDatumDo() {
        return datumDo;
    }

    public int getOvlastenikId() {
        return ovlastenikId;
    }

    public String getNapomena() {
        return napomena;
    }

    public String getMemo() {
        return memo;
    }

    public String getOvlastenik() {
        return ovlastenik;
    }

    public int getDani() {
        return dani;
    }

    public int getRadniDani() {
        return radniDani;
    }

    public int getGodina() {
        return godina;
    }
}
