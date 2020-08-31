package nl.hu.dp;

import nl.hu.dp.model.Reiziger;
import nl.hu.dp.model.ReizigerDAO;
import nl.hu.dp.model.ReizigerDAOPsql;

import java.sql.*;
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
            testReizigerDAO(new ReizigerDAOPsql(connection));
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
        Reiziger reiziger = null;
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", java.sql.Date.valueOf(gbdatum));
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
        sietske.setGeboortedatum(Date.valueOf("2020-10-10"));
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

}
