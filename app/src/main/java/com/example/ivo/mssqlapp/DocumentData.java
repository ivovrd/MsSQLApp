package com.example.ivo.mssqlapp;

import java.io.Serializable;

/**
 * Created by Ivo on 28.8.2015..
 */
public class DocumentData implements Serializable{
    private int ZaposlenikId, OvlastenikId, KorisnikId, Status, Dani, RadniDani, Godina;
    private String Sifra, TipDokumenta, DatumOd, DatumDo, Napomena, Memo;

    public DocumentData(String sifra, String tipDokumenta, int zaposlenikId, int ovlastenikId, int korisnikId, int status, int dani, int radniDani, int godina, String datumOd, String datumDo, String napomena, String memo){
        this.Sifra = sifra;
        this.TipDokumenta = tipDokumenta;
        this.ZaposlenikId = zaposlenikId;
        this.OvlastenikId = ovlastenikId;
        this.KorisnikId = korisnikId;
        this.Status = status;
        this.Dani = dani;
        this.RadniDani = radniDani;
        this.Godina = godina;
        this.DatumOd = datumOd;
        this.DatumDo = datumDo;
        this.Napomena = napomena;
        this.Memo = memo;
    }

    public DocumentData(String sifra, int status){
        this.Sifra = sifra;
        this.Status = status;
    }

    public String getSifra() {
        return Sifra;
    }

    public String getTipDokumenta() {
        return TipDokumenta;
    }

    public int getZaposlenikId() {
        return ZaposlenikId;
    }

    public int getOvlastenikId() {
        return OvlastenikId;
    }

    public int getKorisnikId() {
        return KorisnikId;
    }

    public int getStatus() {
        return Status;
    }

    public int getDani() {
        return Dani;
    }

    public int getRadniDani() {
        return RadniDani;
    }

    public int getGodina() {
        return Godina;
    }

    public String getDatumOd() {
        return DatumOd;
    }

    public String getDatumDo() {
        return DatumDo;
    }

    public String getNapomena() {
        return Napomena;
    }

    public String getMemo() {
        return Memo;
    }
}
