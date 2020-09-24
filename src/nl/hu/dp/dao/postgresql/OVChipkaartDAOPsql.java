package nl.hu.dp.dao.postgresql;

import nl.hu.dp.dao.OVChipkaartDAO;
import nl.hu.dp.dao.ReizigerDAO;
import nl.hu.dp.domain.OVChipkaart;
import nl.hu.dp.domain.Product;
import nl.hu.dp.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {

    private Connection connection;
    private ReizigerDAO rdao;

    public OVChipkaartDAOPsql(Connection connection, ReizigerDAO rdao) {
        this.connection = connection;
        this.rdao = rdao;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {
        // Save Reiziger if he doesn't exist
        if (rdao.findById(ovChipkaart.getReiziger().getId()) == null)
            return rdao.save(ovChipkaart.getReiziger());

        // SQL Query
        String query = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());
        preparedStatement.setDate(2, Date.valueOf(ovChipkaart.getGeldig_tot()));
        preparedStatement.setInt(3, ovChipkaart.getKlasse());
        preparedStatement.setDouble(4, ovChipkaart.getSaldo());
        preparedStatement.setInt(5, ovChipkaart.getReiziger().getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Save relatie met Product
        for (Product product : ovChipkaart.getProducten()) {

            query = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());
            preparedStatement.setInt(2, product.getProduct_nummer());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        // SQL Query
        String query = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ? WHERE kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDate(1, Date.valueOf(ovChipkaart.getGeldig_tot()));
        preparedStatement.setInt(2, ovChipkaart.getKlasse());
        preparedStatement.setDouble(3, ovChipkaart.getSaldo());
        preparedStatement.setInt(4, ovChipkaart.getKaart_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Save de relaties met nieuwe Producten
        for (Product product : ovChipkaart.getProducten()) {

            // Select de relatie uit DB
            query = "SELECT * FROM ov_chipkaart_product WHERE product_nummer = ? AND kaart_nummer = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, product.getProduct_nummer());
            preparedStatement.setInt(2, ovChipkaart.getKaart_nummer());

            // Resultaten
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean next = resultSet.next();

            // Sluit alles
            resultSet.close();
            preparedStatement.close();

            // Maak geen relatie aan als deze al bestaat
            if (next)
                continue;

            // Persisteer relatie
            query = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());
            preparedStatement.setInt(2, product.getProduct_nummer());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }

        // Verwijder de relaties met nietbestaande Producten
        // Select alle relaties van deze kaart
        query = "SELECT * FROM ov_chipkaart_product WHERE kaart_nummer = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();
        int productnummer;

        while (resultSet.next()) {
            productnummer = resultSet.getInt("product_nummer");

            // Vergelijk alle kaartnummers
            boolean match = false;
            for (Product product : ovChipkaart.getProducten()) {
                if (product.getProduct_nummer() == productnummer) {
                    match = true;
                    break;
                }
            }

            // Als het nummer uit de DB geen nummer matched uit memory, moet de relatie uit de db gedelete worden
            if (!match) {

                // SQL Query
                query = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(query);
                preparedStatement2.setInt(1, productnummer);

                // Query uitvoeren
                preparedStatement2.executeUpdate();

                // Sluit alles
                preparedStatement2.close();

            }

        }

        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        // SQL Query
        String query = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // SQL Query
        query = "DELETE FROM ov_chipkaart where kaart_nummer = ?;";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

        // Query uitvoeren
        preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public OVChipkaart findByKaartNummer(int kaart_nummer) throws SQLException {
        // SQL Query
        String query = "SELECT klasse, geldig_tot, saldo, reiziger_id, p.product_nummer AS product_nummer, naam, beschrijving, prijs " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "LEFT JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer " +
                "WHERE o.kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, kaart_nummer);

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int reiziger_id, klasse;
        Date geldig_tot;
        double saldo;
        OVChipkaart ovChipkaart = null;

        int product_nummer;
        String naam, beschrijving;
        double prijs;
        Product product;

        while (resultSet.next()) {

            // Als het de 1e result is, maak het ovkaart object
            if (ovChipkaart == null) {
                klasse = resultSet.getInt("klasse");
                geldig_tot = resultSet.getDate("geldig_tot");
                saldo = resultSet.getDouble("saldo");
                reiziger_id = resultSet.getInt("reiziger_id");
                ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, rdao.findById(reiziger_id));
            }

            // Voeg product toe
            product_nummer = resultSet.getInt("product_nummer");
            naam = resultSet.getString("naam");
            beschrijving = resultSet.getString("beschrijving");
            prijs = resultSet.getDouble("prijs");
            product = new Product(product_nummer, naam, beschrijving, prijs);

            ovChipkaart.addProduct(product);

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return ovChipkaart;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        // SQL Query
        String query = "SELECT klasse, geldig_tot, saldo, reiziger_id, p.product_nummer AS product_nummer, naam, beschrijving, prijs, o.kaart_nummer AS kaart_nummer " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "LEFT JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer " +
                "WHERE o.reiziger_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int kaart_nummer, klasse;
        Date geldig_tot;
        double saldo;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        int product_nummer;
        String naam, beschrijving;
        double prijs;
        Product product;

        while (resultSet.next()) {

            kaart_nummer = resultSet.getInt("kaart_nummer");
            klasse = resultSet.getInt("klasse");
            geldig_tot = resultSet.getDate("geldig_tot");
            saldo = resultSet.getDouble("saldo");

            OVChipkaart ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, reiziger);

            product_nummer = resultSet.getInt("product_nummer");
            naam = resultSet.getString("naam");
            beschrijving = resultSet.getString("beschrijving");
            prijs = resultSet.getDouble("prijs");
            product = new Product(product_nummer, naam, beschrijving, prijs);

            // Check of kaart met deze key al in lijst zit, zo ja: voeg alle producten aan deze kaart toe
            boolean contains = false;
            for (OVChipkaart ovkaart : ovChipkaarten) {
                if (ovChipkaart.getKaart_nummer() == ovChipkaart.getKaart_nummer()) {
                    ovChipkaart = ovkaart;
                    contains = true;
                    break;
                }
            }

            // Als kaart niet in lijst zit, voeg toe
            if (!contains)
                ovChipkaarten.add(ovChipkaart);

            // Voeg product toe
            ovChipkaart.addProduct(product);

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        // SQL Query
        String query = "SELECT p.product_nummer AS product_nummer, naam, beschrijving, prijs, o.kaart_nummer AS kaart_nummer, klasse, saldo, geldig_tot, reiziger_id " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "INNER JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer ORDER BY product_nummer;";
        Statement statement = connection.createStatement();

        // Resultaten
        ResultSet resultSet = statement.executeQuery(query);

        int kaart_nummer, klasse, reiziger_id;
        Date geldig_tot;
        double saldo;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        int product_nummer;
        String naam, beschrijving;
        double prijs;
        Product product;

        while (resultSet.next()) {

            kaart_nummer = resultSet.getInt("kaart_nummer");
            klasse = resultSet.getInt("klasse");
            geldig_tot = resultSet.getDate("geldig_tot");
            saldo = resultSet.getDouble("saldo");
            reiziger_id = resultSet.getInt("reiziger_id");

            OVChipkaart ovChipkaart = new OVChipkaart(kaart_nummer, geldig_tot.toLocalDate(), klasse, saldo, rdao.findById(reiziger_id));

            product_nummer = resultSet.getInt("product_nummer");
            naam = resultSet.getString("naam");
            beschrijving = resultSet.getString("beschrijving");
            prijs = resultSet.getDouble("prijs");
            product = new Product(product_nummer, naam, beschrijving, prijs);

            // Check of kaart met deze key al in lijst zit, zo ja: voeg alle producten aan deze kaart toe
            boolean contains = false;
            for (OVChipkaart ovkaart : ovChipkaarten) {
                if (ovChipkaart.getKaart_nummer() == ovChipkaart.getKaart_nummer()) {
                    ovChipkaart = ovkaart;
                    contains = true;
                    break;
                }
            }

            // Als kaart niet in lijst zit, voeg toe
            if (!contains)
                ovChipkaarten.add(ovChipkaart);

            // Voeg product toe
            ovChipkaart.addProduct(product);

        }
        // Sluit alles
        resultSet.close();
        statement.close();

        // Return
        return ovChipkaarten;
    }
}
