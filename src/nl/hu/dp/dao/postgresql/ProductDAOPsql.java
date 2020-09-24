package nl.hu.dp.dao.postgresql;

import nl.hu.dp.dao.AdresDAO;
import nl.hu.dp.dao.OVChipkaartDAO;
import nl.hu.dp.dao.ProductDAO;
import nl.hu.dp.dao.ReizigerDAO;
import nl.hu.dp.domain.Adres;
import nl.hu.dp.domain.OVChipkaart;
import nl.hu.dp.domain.Product;
import nl.hu.dp.domain.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {

    private Connection connection;
    private OVChipkaartDAO odao;

    public ProductDAOPsql(Connection connection, OVChipkaartDAO odao) {
        this.connection = connection;
        this.odao = odao;
    }

    @Override
    public boolean save(Product product) throws SQLException {
        // SQL Query
        String query = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, product.getProduct_nummer());
        preparedStatement.setString(2, product.getNaam());
        preparedStatement.setString(3, product.getBeschrijving());
        preparedStatement.setDouble(4, product.getPrijs());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Save relatie met OVChipkaart
        for (OVChipkaart ovChipkaart : product.getOVChipkaarten()) {
            if (odao.findByKaartNummer(ovChipkaart.getKaart_nummer()) == null)
                odao.save(ovChipkaart);

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
    public boolean update(Product product) throws SQLException {
        // SQL Query
        String query = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, product.getNaam());
        preparedStatement.setString(2, product.getBeschrijving());
        preparedStatement.setDouble(3, product.getPrijs());
        preparedStatement.setInt(4, product.getProduct_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Save de relaties met nieuwe OVChipkaarten
        for (OVChipkaart ovChipkaart : product.getOVChipkaarten()) {

            // Update OVkaart
            odao.update(ovChipkaart);

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

        // Verwijder de relaties met nietbestaande OVChipkaarten
        // Select alle relaties van dit product
        query = "SELECT * FROM ov_chipkaart_product WHERE product_nummer = ?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, product.getProduct_nummer());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();
        int kaartnummer;

        while (resultSet.next()) {
            kaartnummer = resultSet.getInt("kaart_nummer");

            // Vergelijk alle kaartnummers
            boolean match = false;
            for (OVChipkaart ovChipkaart : product.getOVChipkaarten()) {
                if (ovChipkaart.getKaart_nummer() == kaartnummer) {
                    match = true;
                    break;
                }
            }

            // Als het nummer uit de DB geen nummer matched uit memory, moet de relatie uit de db gedelete worden
            if (!match) {

                // SQL Query
                query = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?";
                PreparedStatement preparedStatement2 = connection.prepareStatement(query);
                preparedStatement2.setInt(1, kaartnummer);

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
    public boolean delete(Product product) throws SQLException{
        // SQL Query
        String query = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, product.getProduct_nummer());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // SQL Query
        query = "DELETE FROM product WHERE product_nummer = ?;";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, product.getProduct_nummer());

        // Query uitvoeren
        preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public Product findById(int id) throws SQLException{
        // SQL Query
        String query = "SELECT naam, beschrijving, prijs, o.kaart_nummer AS kaart_nummer " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "LEFT JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer " +
                "WHERE p.product_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        String naam, beschrijving;
        double prijs;
        Product product = null;
        int kaart_nummer;
        OVChipkaart ovChipkaart;

        while (resultSet.next()) {

            // Als het de 1e result is, maak het product object
            if (product == null) {
                naam = resultSet.getString("naam");
                beschrijving = resultSet.getString("beschrijving");
                prijs = resultSet.getDouble("prijs");
                product = new Product(id, naam, beschrijving, prijs);
            }

            // Voeg OVkaart toe
            kaart_nummer = resultSet.getInt("kaart_nummer");
            ovChipkaart = odao.findByKaartNummer(kaart_nummer);
            if (ovChipkaart != null)
                product.addOVChipkaart(ovChipkaart);

        }



        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return product;
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException{
        // SQL Query
        String query = "SELECT naam, beschrijving, prijs, p.product_nummer AS product_nummer " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "LEFT JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer " +
                "WHERE o.kaart_nummer = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, ovChipkaart.getKaart_nummer());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int product_nummer, kaart_nummer;
        String naam, beschrijving;
        double prijs;
        List<Product> producten = new ArrayList<>();

        while (resultSet.next()) {

            product_nummer = resultSet.getInt("product_nummer");
            naam = resultSet.getString("naam");
            beschrijving = resultSet.getString("beschrijving");
            prijs = resultSet.getDouble("prijs");

            Product product = new Product(product_nummer, naam, beschrijving, prijs);

            // Check of product met deze key al in lijst zit, zo ja: voeg alle kaarten aan dit product toe
            boolean contains = false;
            for (Product prod : producten) {
                if (prod.getProduct_nummer() == product.getProduct_nummer()) {
                    product = prod;
                    contains = true;
                    break;
                }
            }

            // Als product niet in lijst zit, voeg toe
            if (!contains)
                producten.add(product);

            // Voeg OVkaart toe
            product.addOVChipkaart(ovChipkaart);

        }



        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return producten;
    }

    @Override
    public List<Product> findAll() throws SQLException {
        // SQL Query
        String query = "SELECT p.product_nummer AS product_nummer, naam, beschrijving, prijs, o.kaart_nummer AS kaart_nummer " +
                "FROM product p LEFT JOIN ov_chipkaart_product ocp on p.product_nummer = ocp.product_nummer " +
                "LEFT JOIN ov_chipkaart o on o.kaart_nummer = ocp.kaart_nummer ORDER BY product_nummer;";
        Statement statement = connection.createStatement();

        // Resultaten
        ResultSet resultSet = statement.executeQuery(query);

        int product_nummer, kaart_nummer;
        String naam, beschrijving;
        double prijs;
        List<Product> producten = new ArrayList<>();

        while (resultSet.next()) {

            product_nummer = resultSet.getInt("product_nummer");
            naam = resultSet.getString("naam");
            beschrijving = resultSet.getString("beschrijving");
            prijs = resultSet.getDouble("prijs");

            Product product = new Product(product_nummer, naam, beschrijving, prijs);

            // Check of product met deze key al in lijst zit, zo ja: voeg alle kaarten aan dit product toe
            boolean contains = false;
            for (Product prod : producten) {
                if (prod.getProduct_nummer() == product.getProduct_nummer()) {
                    product = prod;
                    contains = true;
                    break;
                }
            }

            // Als product niet in lijst zit, voeg toe
            if (!contains)
                producten.add(product);

            // Voeg OVkaart toe
            kaart_nummer = resultSet.getInt("kaart_nummer");
            OVChipkaart ovChipkaart = odao.findByKaartNummer(kaart_nummer);
            if (ovChipkaart != null)
                product.addOVChipkaart(ovChipkaart);

        }

        // Sluit alles
        resultSet.close();
        statement.close();

        // Return
        return producten;
    }

}
