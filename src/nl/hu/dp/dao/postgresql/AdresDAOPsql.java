package nl.hu.dp.dao.postgresql;

import nl.hu.dp.dao.AdresDAO;
import nl.hu.dp.dao.ReizigerDAO;
import nl.hu.dp.domain.Adres;
import nl.hu.dp.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {

    private Connection connection;
    private ReizigerDAO rdao;

    public AdresDAOPsql(Connection connection, ReizigerDAO rdao) {
        this.connection = connection;
        this.rdao = rdao;
    }

    @Override
    public boolean save(Adres adres) throws SQLException {
        // Save Reiziger if he doesn't exist
        if (rdao.findById(adres.getReiziger().getId()) == null)
            return rdao.save(adres.getReiziger());

        // SQL Query
        String query = "INSERT INTO adres (adres_id, straat, huisnummer, postcode, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, adres.getId());
        preparedStatement.setString(2, adres.getStraat());
        preparedStatement.setString(3, adres.getHuisnummer());
        preparedStatement.setString(4, adres.getPostcode());
        preparedStatement.setString(5, adres.getWoonplaats());
        preparedStatement.setInt(6, adres.getReiziger().getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean update(Adres adres) throws SQLException {
        // SQL Query
        String query = "UPDATE adres SET straat = ?, huisnummer = ?, postcode = ?, woonplaats = ? WHERE adres_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, adres.getStraat());
        preparedStatement.setString(2, adres.getHuisnummer());
        preparedStatement.setString(3, adres.getPostcode());
        preparedStatement.setString(4, adres.getWoonplaats());
        preparedStatement.setInt(5, adres.getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean delete(Adres adres) throws SQLException{
        // SQL Query
        String query = "DELETE FROM adres where adres_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, adres.getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public Adres findById(int id) throws SQLException{
        // SQL Query
        String query = "SELECT * FROM adres WHERE adres_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int reiziger_id;
        String straat, huisnummer, postcode, woonplaats;
        Adres adres = null;

        while (resultSet.next()) {

            straat = resultSet.getString("straat");
            huisnummer = resultSet.getString("huisnummer");
            postcode = resultSet.getString("postcode");
            woonplaats = resultSet.getString("woonplaats");
            reiziger_id = resultSet.getInt("reiziger_id");

            adres = new Adres(id, straat, huisnummer, postcode, woonplaats, rdao.findById(reiziger_id));

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return adres;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) throws SQLException{
        // SQL Query
        String query = "SELECT * FROM adres WHERE reiziger_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int id;
        String straat, huisnummer, postcode, woonplaats;
        Adres adres = null;

        while (resultSet.next()) {

            id = resultSet.getInt("adres_id");
            straat = resultSet.getString("straat");
            huisnummer = resultSet.getString("huisnummer");
            postcode = resultSet.getString("postcode");
            woonplaats = resultSet.getString("woonplaats");

            adres = new Adres(id, straat, huisnummer, postcode, woonplaats, reiziger);

        }
        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return adres;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        // SQL Query
        String query = "SELECT * FROM adres";
        Statement statement = connection.createStatement();

        // Resultaten
        ResultSet resultSet = statement.executeQuery(query);

        int id, reiziger_id;
        String straat, huisnummer, postcode, woonplaats;
        List<Adres> adressen = new ArrayList<>();

        while (resultSet.next()) {

            id = resultSet.getInt("adres_id");
            straat = resultSet.getString("straat");
            huisnummer = resultSet.getString("huisnummer");
            postcode = resultSet.getString("postcode");
            woonplaats = resultSet.getString("woonplaats");
            reiziger_id = resultSet.getInt("reiziger_id");

            Adres adres = new Adres(id, straat, huisnummer, postcode, woonplaats, rdao.findById(reiziger_id));
            adressen.add(adres);

        }

        // Sluit alles
        resultSet.close();
        statement.close();

        // Return
        return adressen;
    }

}
