package nl.hu.dp.domain;

import java.time.LocalDate;

public class OVChipkaart {

    // Attributes
    private final int kaart_nummer;
    private int klasse;
    private LocalDate geldig_tot;
    private double saldo;

    // One, One relation with Reiziger
    private final Reiziger reiziger;

    // Constructor
    public OVChipkaart(int kaart_nummer, LocalDate geldig_tot, int klasse, double saldo, Reiziger reiziger) {
        this.kaart_nummer = kaart_nummer;
        this.geldig_tot = geldig_tot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
        reiziger.addOVChipkaart(this);
    }

    // Getters and Setters
    public int getKaart_nummer() {
        return kaart_nummer;
    }

    public int getKlasse() {
        return klasse;
    }
    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public LocalDate getGeldig_tot() {
        return geldig_tot;
    }
    public void setGeldig_tot(LocalDate geldig_tot) {
        this.geldig_tot = geldig_tot;
    }

    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    // Other Methods
    public String ownString() {
        return String.format("OVChipkaart #%s: %s %s %s", kaart_nummer, saldo, geldig_tot, klasse);
    }

    @Override
    public String toString() {
        return String.format("OVChipkaart #%s: %s %s %s\n    %s", kaart_nummer, saldo, geldig_tot, klasse, reiziger.ownString());
    }

}
