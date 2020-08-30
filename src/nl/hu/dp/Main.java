package nl.hu.dp;

import java.sql.*;
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

            // SQL Query
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM reiziger";

            // Resultaten
            ResultSet resultSet = statement.executeQuery(query);

            int id;
            String voorletters, tussenvoegsel, achternaam;
            Date geboortedatum;

            while (resultSet.next()) {

                id = resultSet.getInt("reiziger_id");
                voorletters = resultSet.getString("voorletters");
                tussenvoegsel = resultSet.getString("tussenvoegsel");
                achternaam = resultSet.getString("achternaam");
                geboortedatum = resultSet.getDate("geboortedatum");

                System.out.printf("ID: %s, Naam: %s %s %s, Geboren: %s%n", id, voorletters, tussenvoegsel != null ? tussenvoegsel : "", achternaam, geboortedatum);

            }

            // Sluit alles
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException sqlex) {

            System.err.println("[SQLException] Reizigers data kon niet worden opgehaald: " + sqlex.getMessage());

        }

    }

}
