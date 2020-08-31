package nl.hu.dp.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {

    private Connection connection;

    public ReizigerDAOPsql(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException{
        // SQL Query
        String query = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());
        preparedStatement.setString(2, reiziger.getVoorletters());
        preparedStatement.setString(3, reiziger.getTussenvoegsel());
        preparedStatement.setString(4, reiziger.getAchternaam());
        preparedStatement.setDate(5, reiziger.getGeboortedatum());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException{
        // SQL Query
        String query = "UPDATE reiziger SET reiziger_id = ?, voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());
        preparedStatement.setString(2, reiziger.getVoorletters());
        preparedStatement.setString(3, reiziger.getTussenvoegsel());
        preparedStatement.setString(4, reiziger.getAchternaam());
        preparedStatement.setDate(5, reiziger.getGeboortedatum());
        preparedStatement.setInt(6, reiziger.getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException{
        // SQL Query
        String query = "DELETE FROM reiziger where reiziger_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, reiziger.getId());

        // Query uitvoeren
        int affectedRows = preparedStatement.executeUpdate();

        // Sluit alles
        preparedStatement.close();

        // Return
        return affectedRows > 0;
    }

    @Override
    public Reiziger findById(int id) throws SQLException{
        // SQL Query
        String query = "SELECT * FROM reiziger WHERE reiziger_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        String voorletters, tussenvoegsel, achternaam;
        Date geboortedatum;
        Reiziger reiziger = null;

        while (resultSet.next()) {

            voorletters = resultSet.getString("voorletters");
            tussenvoegsel = resultSet.getString("tussenvoegsel");
            achternaam = resultSet.getString("achternaam");
            geboortedatum = resultSet.getDate("geboortedatum");

            reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatum);
            new AdresDAOPsql(connection).findByReiziger(reiziger);
        }

        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return reiziger;
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) throws SQLException{
        // SQL Query
        String query = "SELECT * FROM reiziger WHERE geboortedatum = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDate(1, Date.valueOf(datum));

        // Resultaten
        ResultSet resultSet = preparedStatement.executeQuery();

        int id;
        String voorletters, tussenvoegsel, achternaam;
        Date geboortedatum;
        List<Reiziger> reizigers = new ArrayList<>();

        while (resultSet.next()) {

            id = resultSet.getInt("reiziger_id");
            voorletters = resultSet.getString("voorletters");
            tussenvoegsel = resultSet.getString("tussenvoegsel");
            achternaam = resultSet.getString("achternaam");
            geboortedatum = resultSet.getDate("geboortedatum");

            Reiziger reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatum);
            new AdresDAOPsql(connection).findByReiziger(reiziger);
            reizigers.add(reiziger);

        }

        // Sluit alles
        resultSet.close();
        preparedStatement.close();

        // Return
        return reizigers;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        // SQL Query
        String query = "SELECT * FROM reiziger";
        Statement statement = connection.createStatement();

        // Resultaten
        ResultSet resultSet = statement.executeQuery(query);

        int id;
        String voorletters, tussenvoegsel, achternaam;
        Date geboortedatum;
        List<Reiziger> reizigers = new ArrayList<>();

        while (resultSet.next()) {

            id = resultSet.getInt("reiziger_id");
            voorletters = resultSet.getString("voorletters");
            tussenvoegsel = resultSet.getString("tussenvoegsel");
            achternaam = resultSet.getString("achternaam");
            geboortedatum = resultSet.getDate("geboortedatum");

            Reiziger reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatum);
            new AdresDAOPsql(connection).findByReiziger(reiziger);
            reizigers.add(reiziger);

        }

        // Sluit alles
        resultSet.close();
        statement.close();

        // Return
        return reizigers;
    }

}
