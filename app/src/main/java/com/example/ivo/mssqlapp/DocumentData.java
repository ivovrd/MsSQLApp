package com.example.ivo.mssqlapp;

/**
 * Created by Ivo on 28.8.2015..
 */
public class DocumentData {
    public int Sifra, TipDokumenta, ZaposlenikId, OvlastenikId, KorisnikId, Status, Dani, RadniDani;
    public String Datum, DatumOd, DatumDo, Napomena, Memo;

    public DocumentData(int sifra, int tipDokumenta, int zaposlenikId, int ovlastenikId, int korisnikId, int status, int dani, int radniDani, String datum, String datumOd, String datumDo, String napomena, String memo){
        this.Sifra = sifra;
        this.TipDokumenta = tipDokumenta;
        this.ZaposlenikId = zaposlenikId;
        this.OvlastenikId = ovlastenikId;
        this.KorisnikId = korisnikId;
        this.Status = status;
        this.Dani = dani;
        this.RadniDani = radniDani;
        this.Datum = datum;
        this.DatumOd = datumOd;
        this.DatumDo = datumDo;
        this.Napomena = napomena;
        this.Memo = memo;
    }
}
