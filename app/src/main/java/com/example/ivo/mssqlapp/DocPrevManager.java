package com.example.ivo.mssqlapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivo on 1.7.2015..
 */
public class DocPrevManager {
    private static DocPrevManager mInstance;
    private List<DocPrev> docPrevList;

    public static DocPrevManager getInstance(){
        if(mInstance == null){
            mInstance = new DocPrevManager();
        }
        return mInstance;
    }

    public List<DocPrev> getDocPrevs(){
        return docPrevList;
    }

    public void setDocPrevs(String sifra, String datum, String datumOd, String datumDo, String napomena, String memo, String ovlastenik, int ovlastenikId, int dani, int radniDani, int godina){
        if(docPrevList == null) {
            docPrevList = new ArrayList<>();
        }
        DocPrev docPrev = new DocPrev();

        docPrev.sifra = sifra;
        docPrev.datum = datum;
        docPrev.datumOd = datumOd;
        docPrev.datumDo = datumDo;
        docPrev.ovlastenikId = ovlastenikId;
        docPrev.napomena = napomena;
        docPrev.memo = memo;
        docPrev.ovlastenik = ovlastenik;
        docPrev.dani = dani;
        docPrev.radniDani = radniDani;
        docPrev.godina = godina;

        docPrevList.add(docPrev);
    }
}
