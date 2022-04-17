package simo.com.alco.fragments.scrolltable;

/**
 * Created by icewind on 03.10.2017.
 */

class TableDataObject {

    String header1;
    String header2;
    String header3;
    String header4;
    String header5;
    String header6;
    String header7;
    String header8;
    String header9;
    String header10;
    String header11;
    String header12;
    String header13;
    String header14;

    String[] raw;

    /**
     *
     * @param headers
     */
    TableDataObject(String[] headers){
        this.header1 = headers[0];
        this.header2 = headers[1];
        this.header3 = headers[2];
        this.header4 = headers[3];
        this.header5 = headers[4];
        this.header6 = headers[5];
        this.header7 = headers[6];
        this.header8 = headers[7];
        this.header9 = headers[8];
        this.header10 = headers[9];
        this.header11 = headers[10];
        this.header12 = headers[11];
        this.header13 = headers[12];
        this.header14 = headers[13];
        raw = headers;
    }

    TableDataObject(String header1, String header2, String header3,
                        String header4, String header5, String header6,
                        String header7, String header8, String header9, String header10){

        this.header1 = header1;
        this.header2 = header2;
        this.header3 = header3;
        this.header4 = header4;
        this.header5 = header5;
        this.header6 = header6;
        this.header7 = header7;
        this.header8 = header8;
        this.header9 = header9;

    }

    String[] getRaw() {
        return raw;
    }
}