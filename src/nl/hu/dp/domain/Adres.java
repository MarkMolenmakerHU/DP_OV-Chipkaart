package nl.hu.dp.domain;

public class Adres {

    // Attributes
    private final int id; // PK, dus final
    private String postcode, huisnummer, straat, woonplaats;

    // One, One relation to Reiziger. Kan niet zonder reiziger bestaan, dus final
    private final Reiziger reiziger;

    // Constructor
    public Adres(int id, String postcode, String huisnummer, String straat, String woonplaats, Reiziger reiziger) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reiziger = reiziger;
        reiziger.setAdres(this);
    }

    // Getters and Setters
    public int getId() {
        return id;
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

    // Other Methods
    public String ownString() {
        return String.format("Adres #%s: %s %s %s %s", id, straat, huisnummer, postcode, woonplaats);
    }

    @Override
    public String toString() {
        return String.format("Adres #%s: %s %s %s %s\n    %s", id, straat, huisnummer, postcode, woonplaats, reiziger.ownString());
    }
}
