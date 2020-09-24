package nl.hu.dp;

import nl.hu.dp.dao.AdresDAO;
import nl.hu.dp.dao.OVChipkaartDAO;
import nl.hu.dp.dao.ProductDAO;
import nl.hu.dp.dao.ReizigerDAO;
import nl.hu.dp.dao.postgresql.AdresDAOPsql;
import nl.hu.dp.dao.postgresql.OVChipkaartDAOPsql;
import nl.hu.dp.dao.postgresql.ProductDAOPsql;
import nl.hu.dp.dao.postgresql.ReizigerDAOPsql;
import nl.hu.dp.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        // Verbinding URL voor ovchip Database
        String url = "jdbc:postgresql://localhost/ovchip";
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");

        try {

            Connection connection = DriverManager.getConnection(url, properties);
            ReizigerDAO rdao = new ReizigerDAOPsql(connection);
            AdresDAO adao = new AdresDAOPsql(connection, rdao);
            OVChipkaartDAO odao = new OVChipkaartDAOPsql(connection, rdao);
            ProductDAO pdao = new ProductDAOPsql(connection, odao);

            //testReizigerDAO(rdao);
            //testAdresDAO(adao, rdao);
            //testOVChipkaartDAO(odao, rdao);
            testProductDAO(pdao, rdao, odao);

            connection.close();

        } catch (SQLException throwables) {

            throwables.printStackTrace();

        }

    }

    /**
     * P2. Reiziger DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de Reiziger DAO
     *
     * @throws SQLException - sqlexception
     */
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        Reiziger reiziger;
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse(gbdatum));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
        // Find reiziger door geboortedatum te specificeren
        System.out.println("[Test] ReizigerDAO.findByGbdatum() geeft de volgende reizigers:");
        reizigers = rdao.findByGbdatum(gbdatum);
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Find reiziger door id te specificeren
        System.out.println("[Test] ReizigerDAO.findById() geeft de volgende reizigers:");
        reiziger = rdao.findById(sietske.getId());
        System.out.println(reiziger);
        System.out.println();

        // Update reiziger
        System.out.println("[Test] Eerst \n" + rdao.findById(77) + "\nNa ReizigerDAO.update():");
        sietske.setVoorletters("T");
        sietske.setGeboortedatum(LocalDate.parse("2020-10-10"));
        rdao.update(sietske);
        reiziger = rdao.findById(77);
        System.out.println(reiziger + "\n");

        // Delete reiziger
        reizigers = rdao.findAll();
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
    }

    /**
     * P3. Adres DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de Adres DAO
     * en de relatie tussen de Adres- en Reiziger DAO
     *
     * @throws SQLException - sqlexception
     */
    private static void testAdresDAO(AdresDAO adao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- TestAdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres adres : adressen) {
            System.out.println(adres);
        }
        System.out.println();

        // Maak een nieuw adres aan en persisteer deze in de database
        Reiziger bob = new Reiziger(10, "B", "", "Blokhout", LocalDate.parse("2007-09-11"));
        rdao.save(bob);
        Adres javastraat = new Adres(10, "2805TD", "5", "javastraat", "New Jork", bob);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adao.save(javastraat);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Maak een nieuw adres aan en persisteer deze in de database zonder dat de reiziger in de database staat
        Reiziger klaas = new Reiziger(11, "K", "", "Bootje", LocalDate.parse("2001-09-11"));
        Adres klaasstraat = new Adres(11, "2005AB", "5", "klaasstraat", "New Jersie", klaas);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adao.save(klaasstraat);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Find adres door id te specificeren
        System.out.println("[Test] AdresDAO.findById() geeft het volgende adres:");
        System.out.println(adao.findById(10));
        System.out.println();

        // Find adres door reiziger_id te specificeren
        System.out.println("[Test] AdresDAO.findByReiziger() geeft het volgende adres:");
        System.out.println(adao.findByReiziger(bob));
        System.out.println();

        // Update adres
        System.out.println("[Test] Eerst \n" + adao.findById(10) + "\nNa AdresDAO.update():");
        javastraat.setHuisnummer("1000");
        javastraat.setPostcode("1000AB");
        adao.update(javastraat);
        System.out.println(adao.findById(10) + "\n");

        // Delete adres
        adressen = adao.findAll();
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        adao.delete(javastraat);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Delete reiziger
        adressen = adao.findAll();
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        rdao.delete(klaas);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Delete reizger
        rdao.delete(bob);
    }

    /**
     * P4. OVChipkaart DAO: persistentie van een klasse
     *
     * Deze methode test de CRUD-functionaliteit van de OVChipkaart DAO
     * en de relatie tussen de OVChipkaart(en)- en Reiziger DAO
     *
     * @throws SQLException - sqlexception
     */
    private static void testOVChipkaartDAO(OVChipkaartDAO odao, ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- TestOVChipkaartDAO -------------");

        // Haal alle OVChipkaarten op uit de database
        List<OVChipkaart> ovChipkaarten = odao.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende OVChipkaarten:");
        for (OVChipkaart ovChipkaart : ovChipkaarten) {
            System.out.println(ovChipkaart);
        }
        System.out.println();

        // Maak een nieuw ovchipkaart aan en persisteer deze in de database
        Reiziger jack = new Reiziger(20, "J", "", "Jezus", LocalDate.parse("1889-01-01"));
        rdao.save(jack);
        OVChipkaart ovChipkaart = new OVChipkaart(10101, LocalDate.parse("2020-12-12"), 1, 10.0, jack);
        System.out.print("[Test] Eerst " + ovChipkaarten.size() + " OVKaarten, na OVChipkaartDAO.save() ");
        odao.save(ovChipkaart);
        ovChipkaarten = odao.findAll();
        System.out.println(ovChipkaarten.size() + " OVKaarten\n");

        // Maak een nieuw adres aan en persisteer deze in de database zonder dat de reiziger in de database staat
        Reiziger mark = new Reiziger(21, "M", "", "Mossel", LocalDate.parse("1899-01-01"));
        OVChipkaart markKaart = new OVChipkaart(10102, LocalDate.parse("2021-12-12"), 2, 100.0, mark);
        System.out.print("[Test] Eerst " + ovChipkaarten.size() + " OVKaarten, na OVChipkaartDAO.save() ");
        odao.save(markKaart);
        ovChipkaarten = odao.findAll();
        System.out.println(ovChipkaarten.size() + " OVKaarten\n");

        // Find ovkaart door id te specificeren
        System.out.println("[Test] OVChipkaartDAO.findByKaartNummer() geeft de volgende kaart:");
        System.out.println(odao.findByKaartNummer(10101));
        System.out.println();

        // Find kaart door reiziger_id te specificeren
        System.out.println("[Test] OVChipkaart.findByReiziger() geeft de volgende kaart:");
        System.out.println(odao.findByReiziger(jack));
        System.out.println();

        // Update kaart
        System.out.println("[Test] Eerst \n" + odao.findByKaartNummer(10101) + "\nNa OVChipkaart.update():");
        ovChipkaart.setSaldo(70.71);
        odao.update(ovChipkaart);
        System.out.println(odao.findByKaartNummer(10101) + "\n");

        // Delete kaart
        ovChipkaarten = odao.findAll();
        System.out.print("[Test] Eerst " + ovChipkaarten.size() + " kaarten, na OVChipkaart.delete() ");
        odao.delete(ovChipkaart);
        ovChipkaarten = odao.findAll();
        System.out.println(ovChipkaarten.size() + " kaarten\n");

        // Delete Reiziger
        ovChipkaarten = odao.findAll();
        System.out.print("[Test] Eerst " + ovChipkaarten.size() + " kaarten, na OVChipkaart.delete() ");
        rdao.delete(mark);
        ovChipkaarten = odao.findAll();
        System.out.println(ovChipkaarten.size() + " kaarten\n");

        // Delete Reiziger
        rdao.delete(jack);
    }

    private static void testProductDAO(ProductDAO pdao, ReizigerDAO rdao, OVChipkaartDAO odao) throws SQLException {
        System.out.println("\n---------- TestOVChipkaartDAO -------------");

        // Haal alle OVChipkaarten op uit de database
        List<Product> producten = pdao.findAll();
        System.out.println("[Test] ProductDAO.findAll() geeft de volgende Producten:");
        for (Product product : producten) {
            System.out.println(product);
        }
        System.out.println();

        // Persist een een nieuw product zonder OVkaart
        System.out.println("[Test] Voor ProductDAO.save() eerst " + pdao.findAll().size() + " producten");
        Product p1 = new Product(10, "Product10", "Test Product", 10);
        pdao.save(p1);
        System.out.println("[Test] Na ProductDAO.save() " + pdao.findAll().size() + " producten");
        System.out.println();

        // Delete product
        System.out.println("[Test] Voor ProductDAO.delete() eerst " + pdao.findAll().size() + " producten");
        pdao.delete(p1);
        System.out.println("[Test] Na ProductDAO.delete() " + pdao.findAll().size() + " producten");
        System.out.println();

        // Persist een een nieuw product zonder OVkaart
        System.out.println("[Test] Voor ProductDAO.save() eerst " + pdao.findAll().size() + " producten");
        Product p2 = new Product(11, "Product11", "Test Product", 100);
        Reiziger bob = new Reiziger(100, "B", "van", "Bob", LocalDate.now());
        OVChipkaart ov2 = new OVChipkaart(1000, LocalDate.now(), 1, 10, bob);
        p2.addOVChipkaart(ov2);
        pdao.save(p2);  // Alleen de save in pdao
        System.out.println("[Test] Na ProductDAO.save() " + pdao.findAll().size() + " producten");
        System.out.println();

        System.out.println("[Test] ProductDAO.findByID(), Result of p2 save:");
        System.out.println(pdao.findById(11)); // Resultaat van p2 Save
        System.out.println();

        System.out.println("[Test] ProductDAO.findByOVChipkaart()");
        System.out.println(pdao.findByOVChipkaart(ov2));
        System.out.println();

        // Update product
        System.out.println("[Test] ProductDAO.update() before:");
        System.out.println(pdao.findById(11));
        System.out.println("[Test] ProductDAO.update() after:");
        p2.setNaam("Nieuwe UPDATE naam");
        ov2.setSaldo(666);
        pdao.update(p2);
        System.out.println(pdao.findById(11));
        System.out.println();

        // Delete product
        System.out.println("[Test] Voor ProductDAO.delete() eerst " + pdao.findAll().size() + " producten");
        pdao.delete(p2);
        odao.delete(ov2);
        rdao.delete(bob);
        System.out.println("[Test] Na ProductDAO.delete() " + pdao.findAll().size() + " producten");
        System.out.println();
    }

}
