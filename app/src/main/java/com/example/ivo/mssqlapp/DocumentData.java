package com.example.ivo.mssqlapp;

/**
 * Created by Ivo on 28.8.2015..
 */
public class DocumentData {
    public int ZaposlenikId, OvlastenikId, KorisnikId, Status, Dani, RadniDani;
    public String Sifra, TipDokumenta, DatumOd, DatumDo, Napomena, Memo;

    public DocumentData(String sifra, String tipDokumenta, int zaposlenikId, int ovlastenikId, int korisnikId, int status, int dani, int radniDani, String datumOd, String datumDo, String napomena, String memo){
        this.Sifra = sifra;
        this.TipDokumenta = tipDokumenta;
        this.ZaposlenikId = zaposlenikId;
        this.OvlastenikId = ovlastenikId;
        this.KorisnikId = korisnikId;
        this.Status = status;
        this.Dani = dani;
        this.RadniDani = radniDani;
        this.DatumOd = datumOd;
        this.DatumDo = datumDo;
        this.Napomena = napomena;
        this.Memo = memo;
    }
}
