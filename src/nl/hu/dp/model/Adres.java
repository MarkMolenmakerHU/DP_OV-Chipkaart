package nl.hu.dp.model;

public class Adres {

    int id;
    String postcode, huisnummer, straat, woonplaats;
    Reiziger reiziger;

    public Adres(int id, String postcode, String huisnummer, String straat, String woonplaats, Reiziger reiziger) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reiziger = reiziger;
        reiziger.setAdres(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    @Override
    public String toString() {
        return String.format("Adres #%s: %s %s %s %s - %s", id, straat, huisnummer, postcode, woonplaats, reiziger.toSurfaceString());
    }

    public String toSurfaceString() {
        return String.format("Adres #%s: %s %s %s %s", id, straat, huisnummer, postcode, woonplaats);
    }

}
