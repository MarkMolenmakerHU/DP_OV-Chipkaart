package nl.hu.dp.domain;

import java.util.ArrayList;

public class Product {

    // Attributes
    private final int product_nummer;
    private String naam;
    private String beschrijving;
    private double prijs;

    // Many, Many relation with OVChipkaart
    private ArrayList<OVChipkaart> ovChipkaarten = new ArrayList<>();

    // Constructor
    public Product(int product_nummer, String naam, String beschrijving, double prijs) {
        this.product_nummer = product_nummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    // Getters and Setters
    public int getProduct_nummer() {
        return product_nummer;
    }

    public String getNaam() {
        return naam;
    }
    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }
    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }
    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public ArrayList<OVChipkaart> getOVChipkaarten() {
        return ovChipkaarten;
    }
    public void setOVChipkaarten(ArrayList<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }
    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.add(ovChipkaart);
    }
    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        this.ovChipkaarten.remove(ovChipkaart);
    }

    // Other Methods
    public String ownString() {
        return String.format("Product %s#: %s, %s, %s", product_nummer, naam, beschrijving, prijs);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("OVChipkaarten: %s kaart(en)", ovChipkaarten.size()));
        for (OVChipkaart ovchip : ovChipkaarten)
            stringBuilder.append("\n        ").append(ovchip.toString());

        return String.format("%s\n    %s",
                ownString(),
                ovChipkaarten.size() > 0 ? stringBuilder.toString() : "<Geen OVChipkaarten>"
        );
    }
}
